package cn.lliiooll.ppbuff.data.bean;


public class PShareDetail {

    public PShareDetailData data;

    public static class PShareDetailData {
        public PShareDetailDataPost post;
        public PShareDetailDataUser user;

    }

    public static class PShareDetailDataPost {
        public String ip;
    }

    public static class PShareDetailDataUser {
        public PShareDetailDataUserPosition position;
    }

    public static class PShareDetailDataUserPosition {
        public String province;
        public String country;
        public String city;
    }
}
