package com.example.team.mapapplication.business.background_functions.latlng;
public class Math_ways {
    /** 长半径a=6378137 */
    private static double a= 6378137;
    /** 短半径b=6356752.3142 */
    private static double b = 6356752.3142;
    /** 扁率f=1/298.2572236 */
    private static double f = 1 / 298.2572236;

    static double EARTH_RADIUS = 63710000;//km 地球半径 平均值，千米
    //由一点的经纬度，另一点的距离和方位角求另一点的经纬度
   public static JingWei countJW(JingWei jinWei,double distance,double angle) {
        double alpha1 = rad(angle);
        double sinAlpha1 = Math.sin(alpha1);
        double cosAlpha1 = Math.cos(alpha1);
        double tanU1 = (1 - f) * Math.tan(rad(jinWei.latitude_value));
        double cosU1 = 1 / Math.sqrt((1 + tanU1 * tanU1));
        double sinU1 = tanU1 * cosU1;
        double sigma1 = Math.atan2(tanU1, cosAlpha1);
        double sinAlpha = cosU1 * sinAlpha1;
        double cosSqAlpha = 1 - sinAlpha * sinAlpha;
        double uSq = cosSqAlpha * (a*a- b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double cos2SigmaM=0;
        double sinSigma=0;
        double cosSigma=0;
        double sigma = distance / (b * A), sigmaP = 2 * Math.PI;
        while (Math.abs(sigma - sigmaP) > 1e-12) {
            cos2SigmaM = Math.cos(2 * sigma1 + sigma);
            sinSigma = Math.sin(sigma);
            cosSigma = Math.cos(sigma);
            double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)
                    - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
            sigmaP = sigma;
            sigma = distance / (b * A) + deltaSigma;
        }
        double tmp = sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1;
        double lat2 = Math.atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1, (1 - f) * Math.sqrt(sinAlpha * sinAlpha + tmp * tmp));
        double lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1);
        double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
        double L = lambda- (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        double revAz = Math.atan2(sinAlpha, -tmp);
        JingWei jw=new JingWei();
        jw.latitude_value=deg(lat2);
        jw.longitude_value=jinWei.longitude_value+deg(L);
        return jw;
    }

    //由步长确定距离，估计值为0.5米一步
   public static double stepToDistance(double step)
    {
        return step*0.5;
    }

    //由两点的经纬度确定两点的距离
    public static  double translateToDistance(JingWei jw1,JingWei jw2) {
        double Lat1 = rad(jw1.latitude_value); // 纬度

        double Lat2 = rad(jw2.latitude_value);

        double a1 = Lat1 - Lat2;//两点纬度之差

        double b1= rad(jw1.longitude_value) - rad(jw2.longitude_value); //经度之差

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a1/ 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b1/ 2), 2)));//计算两点距离的公式

        s = s * a;//弧长乘地球半径（半径为米）

        s = Math.round(s * 10000d) / 10000d;//精确距离的数值

        return s;
    }
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    private static double deg(double x) {
        return x * 180 / Math.PI;
    }


    public static double HaverSin(double theta)
    {
        double v = Math.sin(theta / 2);
        return v * v;
    }

    public static double Distance(JingWei jw1,JingWei jw2)
    {
        double la1=jw1.latitude_value;
        double la2=jw2.latitude_value;
        double lo1=jw1.longitude_value;
        double lo2=jw2.longitude_value;
       la1= ConvertDegreesToRadians(jw1.latitude_value);
       lo1 = ConvertDegreesToRadians(jw1.longitude_value);
       la2 = ConvertDegreesToRadians(jw2.latitude_value);
       lo2 = ConvertDegreesToRadians(jw2.longitude_value);

        //差值
        double vLon = Math.abs(lo1 - lo2);
        double vLat = Math.abs(la1 - la2);

        //h is the great circle distance in radians, great circle就是一个球体上的切面，它的圆心即是球心的一个周长最大的圆。
        double h = HaverSin(vLat) + Math.cos(la1) * Math.cos(la2) * HaverSin(vLon);

        double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));

        return distance;
    }

    public static double ConvertDegreesToRadians(double degrees)
    {
        return degrees * Math.PI / 180;}
}

