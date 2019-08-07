package com.synics.gymp.bluec;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Created by Administrator on 2019/06/22.
 */

public class DataBean implements Serializable {

    public Boolean OnFieldChange(String fieldName) {
        return false;
    }

    public Object get(String fieldName) {
        Object result = null;
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                result = field.get(this);
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(get)", e.getMessage());
        }
        return result;
    }

    private int objToInt(Object obj) {
        return (obj == null || obj.toString().equals("")) ? 0 : Integer.parseInt(obj.toString());
    }

    private long objToLong(Object obj) {
        return (obj == null || obj.toString().equals("")) ? 0 : Long.parseLong(obj.toString());
    }

    private double objToDouble(Object obj) {
        return (obj == null || obj.toString().equals("")) ? 0 : Double.parseDouble(obj.toString());
    }

    protected int strToInt(String obj) {
        return (obj == null || obj.equals("")) ? 0 : Integer.parseInt(obj.toString());
    }

    protected long strToLong(String obj) {
        return (obj == null || obj.equals("")) ? 0 : Long.parseLong(obj.toString());
    }

    protected double strToDouble(String obj) {
        return (obj == null || obj.equals("")) ? 0.0 : Double.parseDouble(obj.toString());
    }

    public String getString(String fieldName) {
        String result = "";
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                Object obj = field.get(this);
                result = (obj != null) ? obj.toString() : "";
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(getString)", e.getMessage());
        }
        return result;
    }

    public String getString(String fieldName, int nLClen, String afxChar) {
        String result = getString(fieldName);
        int clen = result.length();
        if (clen > nLClen) {
            result = result.substring(nLClen);
            result = CUtil.StringOfChar(afxChar, nLClen) + result;
        }
        return result;
    }

    public String $(String fieldName) {
        return getString(fieldName);
    }

    public String $(String fieldName, int nLClen, String afxChar) {
        return getString(fieldName, nLClen, afxChar);
    }

    public String getRightString(String fieldName, int nClen) {
        String result = getString(fieldName);
        int clen = result.length();
        if (clen > nClen) {
            result = result.substring(clen - nClen);
        }
        return result;
    }

    public int getInt(String fieldName) {
        int result = 0;
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                result = objToInt(field.get(this));
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(int)", e.getMessage());
        }
        return result;
    }

    public int $i(String fieldName) {
        return getInt(fieldName);
    }

    public long getLong(String fieldName) {
        long result = 0;
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                result = objToLong(field.get(this));
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(getLong)", e.getMessage());
        }
        return result;
    }

    public double getDouble(String fieldName) {
        double result = 0;
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                result = objToDouble(field.get(this));
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(getDouble)", e.getMessage());
        }
        return CUtil.roundEx(result, 4);
    }

    public double getDoubleSign(String fieldName) {
        double result = getDouble(fieldName);
        if (result > 0) result = -result;
        return result;
    }

    public String getDouble(String fieldName, String fmt, int width, boolean __zeroToSpace) {
        double v = getDouble(fieldName);
        String result = CUtil.formatNumber(v, fmt, __zeroToSpace);
        if (!result.equals("") && width > 0) {
            int rlen = result.length();
            if (rlen < width) result = CUtil.StringOfChar(" ", width - rlen) + result;
        }
        return result;
    }

    public String getDouble(String fieldName, String fmt) {
        return getDouble(fieldName, fmt, 0, false);
    }

    public String getDouble(String fieldName, String fmt, boolean __zeroToSpace) {
        return getDouble(fieldName, fmt, 0, __zeroToSpace);
    }

    public String getDouble(String fieldName, String fmt, int width) {
        return getDouble(fieldName, fmt, width, false);
    }

    public void setNull(String fieldName) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                switch (CUtil.FieldType(field.getType().getSimpleName())) {
                    case DataType.INT:
                        field.setInt(this, 0);
                        break;
                    case DataType.LONG:
                        field.setLong(this, 0);
                        break;
                    case DataType.DOUBLE:
                        field.setDouble(this, 0);
                        break;
                    default:
                        field.set(this, null);
                        break;
                }
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(setNull)", e.getMessage());
        }
    }

    public void setString(String fieldName, String value) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                switch (CUtil.FieldType(field.getType().getSimpleName())) {
                    case DataType.INT:
                        field.setInt(this, Integer.parseInt(value));
                        break;
                    case DataType.LONG:
                        field.setLong(this,Long.parseLong(value));
                        break;
                    case DataType.DOUBLE:
                        field.setDouble(this,Double.parseDouble(value));
                        break;
                    default:
                        field.set(this, value);
                        break;
                }
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(setString)", e.getMessage());
        }
    }

    public void setInt(String fieldName, int value) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                switch (CUtil.FieldType(field.getType().getSimpleName())) {
                    case DataType.INT:
                        field.setInt(this, value);
                        break;
                    case DataType.LONG:
                        field.setLong(this, value);
                        break;
                    case DataType.DOUBLE:
                        field.setDouble(this, value);
                        break;
                    default:
                        field.set(this, value + "");
                        break;
                }
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(setInt)", e.getMessage());
        }
    }

    public void setLong(String fieldName, long value) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                switch (CUtil.FieldType(field.getType().getSimpleName())) {
                    case DataType.INT:
                        field.setInt(this, (int)value);
                        break;
                    case DataType.LONG:
                        field.setLong(this, value);
                        break;
                    case DataType.DOUBLE:
                        field.setDouble(this, value);
                        break;
                    default:
                        field.set(this, value + "");
                        break;
                }
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(setLong)", e.getMessage());
        }
    }

    public void setDouble(String fieldName, double value) {
        double result = 0;
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                switch (CUtil.FieldType(field.getType().getSimpleName())) {
                    case DataType.INT:
                        field.setInt(this, (int) value);
                        break;
                    case DataType.LONG:
                        field.setLong(this, (long)value);
                        break;
                    case DataType.DOUBLE:
                        field.setDouble(this, value);
                        break;
                    default:
                        field.set(this, value + "");
                        break;
                }
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(setDouble)", e.getMessage());
        }
    }

    /*复位数据,数字设置为0，字符设置为空*/
    public void reset() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String type = field.getGenericType().toString();

                if (type != null) {
                    field.setAccessible(true);
                    switch (CUtil.FieldType(field.getType().getSimpleName())) {
                        case DataType.INT:
                            field.setInt(this, 0);
                            break;
                        case DataType.LONG:
                            field.setLong(this, 0);
                            break;
                        case DataType.DOUBLE:
                            field.setDouble(this, 0);
                            break;
                        default:
                            field.set(this, null);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(reset)", e.getMessage());
        }
    }


    /*复制记录*/
    public void copyFrom(DataBean sdb, String[] noFieldNames) {
        try {
            Boolean b = noFieldNames != null;
            List<String> noFields = null;
            if (b) {
                noFields = Arrays.asList(noFieldNames);
            }
            Field[] fields = this.getClass().getDeclaredFields();
            Field[] sfields = sdb.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (b && noFields.contains(fields[i].getName())) {
                    continue;
                }
                Field field = fields[i];
                field.setAccessible(true);
                Field sfield = sfields[i];
                sfield.setAccessible(true);
                String type = field.getGenericType().toString();
                field.set(this, sfield.get(sdb));
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(copyFrom)", e.getMessage());
        }
    }

    public void copyFromIn(DataBean sdb, String hasFieldNames) {
        try {
            List<String> hasFields = Arrays.asList(hasFieldNames);
            Field[] fields = this.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (hasFields.indexOf(fields[i].getName()) < 0) {
                    continue;
                }
                Field field = fields[i];
                String type = field.getGenericType().toString();

                if (type != null) {
                    field.set(this, field.get(sdb));
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName()+"(copyFromIn)", e.getMessage());
        }
    }

    public String rowToJsonText(List<CField> cFields, boolean bBrace) throws Exception {
        Vector<String> vector = new Vector<>();
        String result = "";
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                CField cfield = cFields.get(i);
                field.setAccessible(true);
                Object o = field.get(this);
                String v = (o == null) ? "" : o.toString();
                v = "\"" + v + "\"";
                /*
                switch (cfield.dataType) {
                    case DataType.INT:
                    case DataType.SHORT:
                    case DataType.DOUBLE:
                        v = (o == null) ? "0" : vector.toString();
                        break;
                    default:
                        v = (o == null) ? "" : vector.toString();
                        v = "\"" + v + "\"";
                }
                */
                vector.add("\"" + fieldName + "\":" + v);
            }
            String[] buf = new String[vector.size()];
            vector.copyInto(buf);
            result = CUtil.joinArray(buf, ",");
        } catch (Exception e) {
           throw new Exception(e.getMessage());
        }
        if (bBrace) result = "{" + result + "}";
        return result;
    }

    public void checkData() throws Exception {
    }
}
