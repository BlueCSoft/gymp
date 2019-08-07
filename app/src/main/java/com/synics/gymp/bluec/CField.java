package com.synics.gymp.bluec;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/30.
 */
public class CField {
    public String fieldName;         // 字段名称
    public int dataType;             // 字段类型

    public String defaultValue = "";  // 默认值
    public String maxValue = "";      // 最大值
    public String minValue = "";      // 最小值

    public int maxLength = 0;         // 最大长度

    public CField() {
    }

    public void setFieldProp(String jsonStr){
        try{
            JSONObject json = new JSONObject(jsonStr);
            if(json.has("defaultvalue")){
                this.defaultValue = json.getString("defaultvalue");
            }
            if(json.has("maxvalue")){
                this.maxValue = json.getString("maxvalue");
            }
            if(json.has("minvalue")){
                this.minValue = json.getString("minvalue");
            }
            if(json.has("maxlength")){
                this.maxLength = json.getInt("maxlength");
            }
        }catch (Exception e){
        }
    }
}
