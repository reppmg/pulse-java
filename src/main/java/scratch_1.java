import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.HashMap;
import java.util.Map;

class Scratch {

    public static void main(String[] args) {
        /***
         * моя часть
         */
        HeartRateRecognizer recognizer = new HeartRateRecognizerImpl(256);
        double pulse = recognizer.detectHeartRate(-13555420.0, 1000L);
        if (pulse > 100) {
            //danger
        }
    }

    /**
     * имплементи
     */
    public interface HeartRateRecognizer {
        int detectHeartRate(double color, long frameTimestamp);

        /***
         * Я так смогу освободить все ресурсы при необходимости после завершения работы с библиотекой
         */
        void destroy();
    }

    /***
     * Твоя реализация детектора пульса
     * Предпочтительнее делать на C++, чтобы было быстрее и эффективнее. В противном случае пиши на Java.
     * Обязательно релизить все аллоцированые объекты
     */
    public static class HeartRateRecognizerImpl implements HeartRateRecognizer {
        private static final int PULSE_TOP_BRODER = 180;
        private static final int PULSE_BOTTOM_BORDER = 55;
        public static final double MILLISECONDS_IN_SECOND = 1000.0;

        /***
         * Работаешь с одним массивом цветов
         */
        private final double[] foreheadColors;
        private final double[] timestamps;
        private final double[] interpolated ;


        private final Map<Integer, Double> freqToIndexMap = new HashMap<>();

        private final int colorBufferSize;

        private int framePos = 0;


        /***
         * Конструктор через который инициализируем детектор, аллоцируем объекты
         * @param colorBufferSize должен быть степенью 2. Требование для преобразования Фурье библиотекой
         */
        public HeartRateRecognizerImpl(int colorBufferSize) {
            this.colorBufferSize = colorBufferSize;
            if (!ArithmeticUtils.isPowerOfTwo(colorBufferSize)) {
                throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING,
                        colorBufferSize);
            }
            this.foreheadColors = new double[colorBufferSize];
            this.timestamps = new double[colorBufferSize];
            this.interpolated = new double[colorBufferSize];

        }

        private void updateFrequenciesMap(double fps) {
            freqToIndexMap.clear();
            int freqsSize = colorBufferSize / 2 + 1;
            for (int i = 0; i < freqsSize; i++) {
                double tmpFreq = 60 * fps / colorBufferSize * i;
                if (tmpFreq > PULSE_BOTTOM_BORDER && tmpFreq < PULSE_TOP_BRODER) {
                    freqToIndexMap.put(i, tmpFreq);
                }
            }
        }


        /***
         * Детектим пульса
         *
         * @param color цвет лба
         * @param frameTimestamp время взятия кадра
         * @return возвращаем текущий пульс, или -1, если еще нету 256 кадров
         */
        @Override
        public int detectHeartRate(double color, long frameTimestamp) {
            if (framePos == colorBufferSize) {
                shiftArrayLeftByOne(foreheadColors);
                shiftArrayLeftByOne(timestamps);
                framePos--;
            }
            timestamps[framePos] = frameTimestamp;
            foreheadColors[framePos++] = color;
            if (framePos < colorBufferSize) {
                return -1;
            }
            double fps = MILLISECONDS_IN_SECOND * (colorBufferSize - 1) / (timestamps[colorBufferSize - 1] - timestamps[0]);
            updateFrequenciesMap(fps);
            interpolate(timestamps, foreheadColors);
            FastFourierTransformer fourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
            // тут не используется window function, так как разницы между результатом с ней и без неё нет.
            Complex[] transform = fourierTransformer.transform(interpolated, TransformType.INVERSE);
            int maxIndex = getMaxIndex(transform);
            return freqToIndexMap.get(maxIndex).intValue();
        }


        void interpolate(double[] x, double[] y) {
            LinearInterpolator interpolator = new LinearInterpolator();
            PolynomialSplineFunction function = interpolator.interpolate(x, y);
            double first = x[0];
            double last = x[x.length - 1];
            double interval = (last - first) / x.length;
            for (int i = 0; i < x.length; i++) {
                interpolated[i] = function.value(first + i * interval);
            }
        }

        private int getMaxIndex(Complex[] transform) {
            double max = 0;
            int maxIndex = -1;
            for (Map.Entry<Integer, Double> indexFreqPair : freqToIndexMap.entrySet()) {
                int index = indexFreqPair.getKey();
                double fftValue = transform[index].abs();
                if (fftValue > max) {
                    max = fftValue;
                    maxIndex = index;
                }
            }
            return maxIndex;
        }

        @SuppressWarnings("SuspiciousSystemArraycopy")
        private void shiftArrayLeftByOne(Object array) {
            System.arraycopy(array, 1, array, 0, colorBufferSize);
        }

        public void destroy() {
            freqToIndexMap.clear();
        }
    }
}