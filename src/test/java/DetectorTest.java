import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class DetectorTest {
    /** значения из середины видео */
    private double[] colors = {174.8954, 174.8709, 175.0380, 175.0391, 174.7892, 174.7735, 174.0856, 174.1038, 174.8105, 174.8396, 174.4435, 174.4737, 174.7582, 174.7582, 175.1862, 175.1849, 174.5921, 174.5994, 174.6370, 174.6486, 174.7075, 174.7075, 173.9340, 173.9684, 174.9684, 174.9736, 174.9764, 174.9850, 174.0905, 174.0905, 174.8527, 174.9169, 174.5946, 174.6033, 174.5397, 174.5399, 174.4215, 174.4209, 174.1086, 174.1523, 174.4932, 174.5129, 174.6779, 174.8179, 174.8417, 174.8785, 174.8775, 174.8775, 174.5685, 174.5571, 174.5367, 174.5367, 174.6124, 174.5847, 173.7660, 173.7884, 174.3873, 174.4027, 174.4449, 174.4592, 173.8609, 173.8459, 174.2489, 174.2864, 174.3947, 174.4006, 174.0295, 174.0295, 175.2701, 175.2802, 174.8148, 174.8279, 174.5894, 174.5943, 174.9339, 174.9290, 174.6037, 174.6103, 174.7275, 174.7481, 175.3095, 175.3404, 174.9107, 174.9096, 174.9198, 174.8870, 174.8260, 174.8380, 173.8981, 173.9054, 174.5755, 174.5765, 174.7833, 174.7484, 174.7109, 174.7004, 174.6541, 174.6152, 173.8823, 173.8871, 174.5079, 174.5054, 174.4709, 174.4880, 174.4106, 174.4106, 175.0468, 175.0615, 174.8923, 174.9294, 174.4017, 174.4257, 174.6690, 174.6072, 174.2323, 174.2321, 174.3309, 174.3309, 174.6041, 174.6538, 174.3664, 174.3426, 174.4105, 174.4441, 174.8903, 174.9062, 174.0822, 174.0822, 174.9320, 174.9379, 174.9839, 175.0098, 174.7386, 174.7352, 175.0522, 175.0684, 174.8090, 174.8543, 174.9971, 175.0300, 174.9457, 174.9404, 174.6003, 174.6267, 174.7062, 174.7080, 174.8295, 174.8418, 175.1417, 175.1566, 175.6700, 175.6720, 174.7740, 174.7584, 174.9710, 174.9791, 175.4549, 175.5151, 174.9445, 175.0084, 174.7627, 174.7553, 175.0362, 175.0362, 174.3323, 174.3295, 174.4615, 174.4921, 174.7096, 174.7196, 174.6634, 174.8057, 174.9930, 175.0104, 174.4862, 174.4846, 174.0099, 174.0082, 174.1458, 174.1678, 173.9371, 173.9342, 174.4045, 174.3998, 174.7662, 174.7531, 174.2845, 174.2973, 174.6682, 174.6678, 174.1479, 174.1418, 173.2475, 173.2480, 173.6704, 173.6657, 173.4928, 173.4928, 174.0550, 174.0550, 174.8818, 174.9207, 174.3605, 174.3955, 174.2777, 174.3389, 174.1105, 174.1880, 174.1627, 174.1007, 174.4783, 174.4850, 174.4372, 174.4425, 174.3646, 174.4620, 174.5692, 174.5572, 173.8899, 173.8939, 174.7956, 174.7956, 174.7359, 174.7966, 174.4745, 174.4898, 175.5023, 175.4820, 174.9500, 174.9613, 174.3947, 174.4245, 174.5946, 174.5824, 174.1987, 174.2067, 174.6129, 174.6279, 174.7008, 174.7198, 174.7673, 174.8125, 174.8844, 174.9324, 174.8269, 174.8049, 174.3200, 174.3291, 174.7397, 174.8121, 174.4292, 174.4292, 175.1978, 175.1956, 175.6012, 175.6012};
    /** colors после применения окна хэмминга */
    private double[] windowed = {13.9916, 14.0141, 14.1008, 14.2230, 14.3733, 14.5912, 14.8004, 15.1166, 15.5421, 15.9567, 16.3791, 16.8879, 17.4692, 18.0696, 18.7623, 19.4565, 20.1282, 20.9116, 21.7434, 22.6162, 23.5391, 24.4973, 25.3851, 26.4281, 27.6652, 28.7918, 29.9580, 31.1644, 32.2421, 33.5166, 34.9803, 36.3467, 37.6660, 39.0886, 40.5281, 42.0145, 43.5030, 45.0506, 46.5447, 48.1604, 49.8897, 51.5575, 53.2959, 55.0546, 56.8021, 58.5774, 60.3621, 62.1678, 63.8800, 65.7162, 67.5661, 69.4398, 71.3595, 73.2514, 74.8149, 76.7434, 78.9437, 80.8958, 82.8692, 84.8368, 86.5056, 88.4623, 90.6392, 92.6332, 94.6673, 96.6469, 98.4118, 100.3798, 103.0734, 105.0524, 106.7349, 108.6971, 110.4917, 112.4278, 114.5723, 116.4826, 118.1626, 120.0492, 121.9976, 123.8633, 126.1004, 127.9430, 129.4254, 131.1992, 132.9592, 134.6630, 136.3197, 138.0074, 138.9086, 140.5301, 142.6652, 144.2302, 145.9367, 147.4117, 148.8516, 150.2811, 151.6452, 152.9805, 153.6661, 154.9618, 156.7739, 157.9925, 159.1433, 160.3015, 161.3324, 162.3937, 164.0097, 165.0049, 165.7832, 166.7130, 167.0583, 167.8854, 168.8800, 169.5352, 169.8390, 170.4606, 171.1329, 171.6619, 172.4137, 172.8982, 173.0007, 173.3162, 173.6749, 173.9514, 174.5913, 174.7536, 174.0275, 174.0762, 174.9259, 174.8829, 174.8312, 174.7107, 174.2450, 173.9983, 174.0216, 173.6972, 173.0518, 172.6608, 172.3180, 171.8193, 171.1587, 170.5292, 169.5285, 168.8391, 168.1554, 167.3518, 166.6178, 165.7354, 165.0804, 164.1125, 163.5663, 162.4992, 160.5642, 159.4055, 158.4141, 157.1972, 156.3589, 155.1090, 153.2689, 151.9529, 150.3347, 148.8895, 147.6545, 146.1479, 144.0285, 142.4641, 140.9795, 139.3830, 137.9060, 136.2367, 134.4909, 132.8725, 131.2618, 129.4992, 127.3197, 125.5070, 123.3400, 121.4954, 119.7299, 117.8676, 115.8221, 113.9175, 112.3056, 110.3717, 108.6588, 106.6973, 104.4556, 102.5013, 100.7482, 98.7728, 96.5068, 94.5300, 92.0810, 90.1186, 88.3730, 86.4085, 84.3669, 82.4169, 80.7346, 78.7932, 77.2262, 75.3121, 73.1574, 71.2709, 69.3367, 67.4895, 65.5481, 63.7408, 61.9137, 60.0939, 58.4433, 56.6862, 54.9347, 53.2241, 51.5137, 49.8808, 48.2757, 46.6646, 44.9135, 43.3714, 42.0760, 40.5875, 39.1183, 37.7096, 36.2548, 34.9077, 33.7884, 32.4998, 31.1581, 29.9554, 28.6966, 27.5792, 26.5232, 25.4797, 24.4260, 23.4716, 22.6116, 21.7423, 20.9238, 20.1429, 19.4101, 18.7222, 18.0827, 17.4866, 16.9220, 16.4130, 15.9092, 15.4993, 15.1718, 14.8622, 14.5625, 14.3437, 14.2359, 14.1135, 14.0726, 14.0481};
    /** пульс, определённый webcam-pulse-detector на этих данных */
    private double pulse = 133.6;

    @Test
    public void testDetector() {
        Scratch.HeartRateRecognizerImpl heartRateRecognizer = new Scratch.HeartRateRecognizerImpl(256);
        int pulseEstimation = feedRecognizer(heartRateRecognizer, colors);
        assertEquals((int) pulse, pulseEstimation);
    }

    @Test
    public void testWindowedValues() {
        Scratch.HeartRateRecognizerImpl heartRateRecognizer = new Scratch.HeartRateRecognizerImpl(256);
        int pulseEstimation = feedRecognizer(heartRateRecognizer, windowed);
        assertEquals((int) pulse, pulseEstimation);
    }

    private int feedRecognizer(Scratch.HeartRateRecognizerImpl heartRateRecognizer, double[] data) {
        for (int i = 0; i < colors.length; i++) {
            int pulseEstimation = heartRateRecognizer.detectHeartRate(data[i]);
            if (pulseEstimation != -1) {
                return pulseEstimation;
            }
        }
        return -1;
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testInput() {
        new Scratch.HeartRateRecognizerImpl(250);
    }

}