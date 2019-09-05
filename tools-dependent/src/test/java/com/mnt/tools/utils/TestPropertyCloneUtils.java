package com.mnt.tools.utils;

import lombok.Builder;
import lombok.Data;
import org.junit.Test;

/**
 * 属性克隆测试类
 */
public class TestPropertyCloneUtils {

    @Test
    public void testClone() {

        TestClass1 tc1 = TestClass1.builder().name("1").lv1(2L).lv2(3).iv1(4).iv2(5).dv1(6.1D).dv2(7.1).fv1(8.1f).fv2(9.1f)
                .bv1(new Byte((byte)1)).bv2((byte)2).cv1(new Character('A')).cv2('B').sv1(new Short((short)5))
                .sv2((short)100).blv1(new Boolean(false)).blv2(true).build();
        TestClass2 tc2 = new TestClass2();
        PropertyCloneUtils.clone(tc1, tc2);
        assert(tc1.dv1.equals(tc2.dv1));
        assert(tc1.dv2 == tc2.dv2);
        assert(tc1.lv1.equals(tc2.lv1));
        assert(tc1.lv2 == tc2.lv2);
        assert(tc1.fv1.equals(tc2.fv1));
        assert(tc1.fv2 == tc2.fv2);
        assert(tc1.iv1.equals(tc2.iv1));
        assert(tc1.iv2 == tc2.iv2);
        assert(tc1.name.equals(tc2.name));
        assert(tc1.bv1.equals(tc2.bv1));
        assert(tc1.bv2 == tc2.bv2);
        assert(tc1.cv1.equals(tc2.cv1));
        assert(tc1.cv2 == tc2.cv2);
        assert(tc1.sv1.equals(tc2.sv1));
        assert(tc1.sv2 == tc2.sv2);
        assert(tc1.blv1.equals(tc2.blv1));
        assert(tc1.blv2 == tc2.blv2);
    }

    /**
     * 测试类1
     */
    @Data
    @Builder
    private static class TestClass1 {

        private String name;

        private Long lv1;

        private long lv2;

        private Integer iv1;

        private int iv2;

        private Double dv1;

        private double dv2;

        private Float fv1;

        private float fv2;

        private Byte bv1;

        private byte bv2;

        private Character cv1;

        private char cv2;

        private Short sv1;

        private short sv2;

        private Boolean blv1;

        private boolean blv2;

    }

    /**
     * 测试类2
     */
    @Data
    private static class TestClass2 {

        private String name;

        private Long lv1;

        private long lv2;

        private Integer iv1;

        private int iv2;

        private Double dv1;

        private double dv2;

        private Float fv1;

        private float fv2;

        private Byte bv1;

        private byte bv2;

        private Character cv1;

        private char cv2;

        private Short sv1;

        private short sv2;

        private Boolean blv1;

        private boolean blv2;

    }

}
