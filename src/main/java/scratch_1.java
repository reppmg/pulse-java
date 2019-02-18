import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.*;
import java.util.stream.Stream;

class Scratch {

    public static void main(String[] args) {
        /***
         * моя часть
         */
        HeartRateRecognizer recognizer = new HeartRateRecognizerImpl(256);
        double pulse = recognizer.detectHeartRate(-13555420.0);
        if (pulse > 100) {
            //danger
        }
    }

    /**
     * имплементи
     */
    public interface HeartRateRecognizer {
        int detectHeartRate(double color);

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

        /***
         * Работаешь с одним массивом цветов
         */
        private final double[] foreheadColors;

        private final Map<Integer, Double> freqToIndexMap = new HashMap<>();

        private final double fps = 30;

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
         * @return возвращаем текущий пульс, или -1, если еще нету 256 кадров
         */
        @Override
        public int detectHeartRate(double color) {
            if (framePos == colorBufferSize) {
                System.arraycopy(foreheadColors, 1, foreheadColors, 0, colorBufferSize);
                framePos--;
            }
            foreheadColors[framePos++] = color;
            if (framePos < colorBufferSize) {
                return -1;
            }
            FastFourierTransformer fourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
            // тут не используется window function, так как разницы между результатом с ней и без неё нет.
            Complex[] transform = fourierTransformer.transform(foreheadColors, TransformType.INVERSE);
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
            return freqToIndexMap.get(maxIndex).intValue();
        }

        public void destroy() {
            freqToIndexMap.clear();
        }
    }
}