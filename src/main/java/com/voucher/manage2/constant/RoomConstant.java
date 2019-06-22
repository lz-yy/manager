package com.voucher.manage2.constant;

import java.util.HashMap;
import java.util.Map;

public class RoomConstant {
    //1:营业房,2:住宅房,3土地
    public static final Map<String, String> PROPERTY_MAP = new HashMap<String, String>() {
        {
            put("1", "营业房");
            put("2", "住宅房");
            put("3", "土地");
        }
    };
    //0,空置,1:已出租.2:可出租,3:不可出租,4已划拨,5自用,6已出售,7已拆迁,8已拆除,9已灭失
    public static final Map<Integer, String> STATE_MAP = new HashMap<Integer, String>() {
        {
            put(0, "空置");
            put(1, "已出租");
            put(2, "可出租");
            put(3, "不可出租");
            put(4, "已划拨");
            put(5, "自用");
            put(6, "已出售");
            put(7, "已拆迁");
            put(8, "已拆除");
            put(9, "已灭失");
        }
    };
    //1:提交申请,2:正在申请,3:申请通过
    public static final Map<String, String> NEATEN_MAP = new HashMap<String, String>() {
        {
            put("1", "提交申请");
            put("2", "正在申请");
            put("3", "申请通过");
        }
    };
    //0:无,1:有
    public static final Map<String, String> HIDDEN_MAP = new HashMap<String, String>() {
        {
            put("0", "无");
            put("1", "有");
        }
    };
    public static final Map<Integer, String> ROW_TYPE_MAP = new HashMap<Integer, String>() {
        {
            put(1, " varchar(50) null ");
            put(2, " int ");
            put(3, " bigint ");
            put(4, " int ");
        }
    };
}
