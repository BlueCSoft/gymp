package com.pax.pay.cup.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;

import com.pax.pay.service.aidl.PayHelper;
import com.synics.gymp.R;
import com.synics.gymp.bluec.CConfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MainActivity extends RoboActivity {

    private PayHelper payHelper = null;
    private AlertDialog dialog;
    private TextView resultTv;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytestmain);
        resultTv = (TextView) findViewById(R.id.result);
        bindservice();
    }

    private void bindservice() {
        if (payHelper == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("服务绑定中请稍等。。。");
            dialog = builder.create();
            dialog.show();
            Intent intent = new Intent();
            intent.setAction("com.pax.pay.SERVICE_ZHZF");
            intent.setPackage(CConfig.APPIDNAME);//"com.pax.up.std"
            bindService(intent, conn, Service.BIND_AUTO_CREATE);
        } else {
            Intent intent = new Intent();
            intent.setAction("com.pax.pay.SERVICE_ZHZF");
            intent.setPackage(CConfig.APPIDNAME);//"com.pax.up.std"
            boolean isSuccess = bindService(intent, conn, Service.BIND_AUTO_CREATE);
            if (isSuccess) {
                Toast.makeText(MainActivity.this, "service  connected OK", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, "服务绑定失败", Toast.LENGTH_SHORT).show();

            }

        }

    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            payHelper = PayHelper.Stub.asInterface(arg1);
            Toast.makeText(MainActivity.this, "service connected OK", Toast.LENGTH_SHORT).show();
            if (dialog.isShowing()) {
                dialog.cancel();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            payHelper = null;
            Log.i("TAG", "----------------onServiceDisconnected");
            if (dialog.isShowing()) {
                dialog.cancel();
            }
        }

    };

    public void OnClickListener(View v) {
        if (payHelper == null) {
            Toast.makeText(MainActivity.this, "服务未绑定", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btnBindService:
                bindservice();
                break;
            case R.id.btnJhPosScan:
                btnJhSale("JH_MICRO_PAY");
                break;
            case R.id.btnJhPhoneScan:
                btnJhSale("JH_NATIVE_PAY");
                break;
            case R.id.btnJhrefund:
                btnJhRefund();
                break;
            case R.id.btnJhrefundQuery:
                btnJhRefundQuery();
                break;
            case R.id.btnJhUnknow:
                btnQueryUnknowTrans();
                break;
            case R.id.btnSale:
                btnSale();
                break;
            case R.id.btnSaleVoid:
                btnSaleVoid();
                break;
            case R.id.btnRefund:
                btnRefund();
                break;
            case R.id.btnAuth:
                btnAuth();
                break;
            case R.id.btnAuthVoid:
                btnAuthVoid();
                break;
            case R.id.btnAuthCM:
                btnAuthCM();
                break;
            case R.id.btnAuthCMAdv:
                btnAuthCMAdv();
                break;
            case R.id.btnAuthCMVoid:
                btnAuthCMVoid();
                break;
            case R.id.btnBalance:
                btnBalance();
                break;
            case R.id.btnLogon:
                btnLogon();
                break;
            case R.id.btnLogout:
                btnLogout();
                break;
            case R.id.btnSettle:
                btnSettle();
                break;
            case R.id.btnPrnLast:
                btnPrnLast();
                break;
            case R.id.btnPrnDetail:
                btnPrnDetail();
                break;
            case R.id.btnPrnTotal:
                btnPrnTotal();
                break;
            case R.id.btnPrnLastBatch:
                btnPrnLastBatch();
                break;
            case R.id.btnPrintAny:
                btnPrintAny();
                break;
            case R.id.btnReadCradNo:
                btnReadCradNo();
                break;
            case R.id.btnSetting:
                btnSetting();
                break;
            case R.id.cancle:
                resultTv.setText("");
                break;
            case R.id.btnBitmap:
                btnBitmap();
                break;
            case R.id.btnQuery:
                btnQuery();
                break;
            case R.id.btnQrSale:
                btnQrSale();
                break;
            case R.id.btnQrVoid:
                btnQrVoid();
                break;
            case R.id.btnQrRefund:
                btnQrRefund();
                break;
            default:
                break;
        }
    }

    private void btnJhSale(final String transType) {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("交易金额");
        //view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("消费").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String transAmount = edit_first.getText().toString();
                        //EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        //final String appId = edit_seconed.getText().toString();

                        if (transAmount == null || transAmount.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                            return;
                        }


//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", transType);
                                    json.put("transAmount", transAmount);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }
    private void btnJhRefund() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_quadra, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);
        TextView view_third = (TextView) textEntryView.findViewById(R.id.view_third);
        TextView view_fourth = (TextView) textEntryView.findViewById(R.id.view_fourth);

        view_first.setText("退款金额");
        view_second.setText("应用ID");
        view_third.setText("总金额");
        view_fourth.setText("平台订单号");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("聚合扫码退款").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        EditText edit_third = (EditText) textEntryView.findViewById(R.id.edit_third);
                        EditText edit_fourth = (EditText) textEntryView.findViewById(R.id.edit_fourth);

                        final String transAmount = edit_first.getText().toString();
                        final String appId = edit_seconed.getText().toString();
                        final String orderAmt = edit_third.getText().toString();
                        final String transactionId = edit_fourth.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "JH_REFUND");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("transAmount", transAmount);
                                    json.put("orderAmount", orderAmt);
                                    json.put("transactionId", transactionId);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }
    private void btnJhRefundQuery() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("退款平台单号");
        //view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("退款查询").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String refundID = edit_first.getText().toString();
                        //EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        //final String appId = edit_seconed.getText().toString();

                        if (refundID == null || refundID.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入退款单号", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "JH_REFUND_QUERY");
                                    json.put("refundID", refundID);
                                    json.put("appId", CConfig.APPIDNAME);//appId

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }
    private void btnQueryUnknowTrans() {

        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("未知交易").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "JH_UNKNOWTRANS");
                                    json.put("appId", CConfig.APPIDNAME);//appId
//                                    json.put("orderNo", orderNo);

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }
private void btnSale() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("交易金额");
        //view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("消费").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String transAmount = edit_first.getText().toString();
                        //EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        //final String appId = edit_seconed.getText().toString();

                        if (transAmount == null || transAmount.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "SALE");
                                    json.put("transAmount", transAmount);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", "666666");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnSaleVoid() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("流水号");
        //view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("消费撤销").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        //EditText edit_second = (EditText) textEntryView.findViewById(R.id.edit_second);
                        final String voucherNo = edit_first.getText().toString();
                        //final String appId = edit_second.getText().toString();

                        if (voucherNo == null || voucherNo.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入流水号", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "VOID");
                                    json.put("voucherNo", voucherNo);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnRefund() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_quadra, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);
        TextView view_third = (TextView) textEntryView.findViewById(R.id.view_third);
        TextView view_fourth = (TextView) textEntryView.findViewById(R.id.view_fourth);

        view_first.setText("交易金额");
        view_second.setText("应用ID");
        view_third.setText("原参考号");
        view_fourth.setText("原交易日期");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("退货").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        EditText edit_third = (EditText) textEntryView.findViewById(R.id.edit_third);
                        EditText edit_fourth = (EditText) textEntryView.findViewById(R.id.edit_fourth);

                        final String transAmount = edit_first.getText().toString();
                        final String appId = edit_seconed.getText().toString();
                        final String origRefNo = edit_third.getText().toString();
                        final String origDate = edit_fourth.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "REFUND");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("transAmount", transAmount);
                                    json.put("origRefNo", origRefNo);
                                    json.put("origDate", origDate);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnAuth() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("交易金额");
        view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("预授权").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String transAmount = edit_first.getText().toString();
                        EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        final String appId = edit_seconed.getText().toString();

                        if (transAmount == null || transAmount.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "AUTH");
                                    json.put("transAmount", transAmount);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnAuthVoid() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_quadra, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);
        TextView view_third = (TextView) textEntryView.findViewById(R.id.view_third);
        TextView view_fourth = (TextView) textEntryView.findViewById(R.id.view_fourth);

        view_first.setText("交易金额");
        view_second.setText("应用ID");
        view_third.setText("原授权码");
        view_fourth.setText("原交易日期");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("预授权撤销").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        EditText edit_third = (EditText) textEntryView.findViewById(R.id.edit_third);
                        EditText edit_fourth = (EditText) textEntryView.findViewById(R.id.edit_fourth);

                        final String transAmount = edit_first.getText().toString();
                        final String appId = edit_seconed.getText().toString();
                        final String origAuthNo = edit_third.getText().toString();
                        final String origDate = edit_fourth.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "AUTH_VOID");
                                    json.put("transAmount", transAmount);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("origAuthNo", origAuthNo);
                                    json.put("origDate", origDate);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnAuthCM() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_quadra, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);
        TextView view_third = (TextView) textEntryView.findViewById(R.id.view_third);
        TextView view_fourth = (TextView) textEntryView.findViewById(R.id.view_fourth);

        view_first.setText("交易金额");
        view_second.setText("应用ID");
        view_third.setText("原授权码");
        view_fourth.setText("原交易日期");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("预授权完成请求").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        EditText edit_third = (EditText) textEntryView.findViewById(R.id.edit_third);
                        EditText edit_fourth = (EditText) textEntryView.findViewById(R.id.edit_fourth);

                        final String transAmount = edit_first.getText().toString();
                        final String appId = edit_seconed.getText().toString();
                        final String origAuthNo = edit_third.getText().toString();
                        final String origDate = edit_fourth.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "AUTH_CM");
                                    json.put("transAmount", transAmount);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("origAuthNo", origAuthNo);
                                    json.put("origDate", origDate);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnAuthCMAdv() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_quadra, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);
        TextView view_third = (TextView) textEntryView.findViewById(R.id.view_third);
        TextView view_fourth = (TextView) textEntryView.findViewById(R.id.view_fourth);

        view_first.setText("交易金额");
        view_second.setText("应用ID");
        view_third.setText("原授权码");
        view_fourth.setText("原交易日期");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("预授权完成通知").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        EditText edit_third = (EditText) textEntryView.findViewById(R.id.edit_third);
                        EditText edit_fourth = (EditText) textEntryView.findViewById(R.id.edit_fourth);

                        final String transAmount = edit_first.getText().toString();
                        final String appId = edit_seconed.getText().toString();
                        final String origAuthNo = edit_third.getText().toString();
                        final String origDate = edit_fourth.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "AUTH_ADV");
                                    json.put("transAmount", transAmount);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("origAuthNo", origAuthNo);
                                    json.put("origDate", origDate);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnAuthCMVoid() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("应用ID");
        view_second.setText("流水号");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("预授权完成请求撤销").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        EditText edit_second = (EditText) textEntryView.findViewById(R.id.edit_second);
                        final String appId = edit_first.getText().toString();
                        final String voucherNo = edit_second.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "AUTH_CM_VOID");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("voucherNo", voucherNo);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnBalance() {

        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("余额查询").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "BALANCE");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnLogon() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("操作员号");
        //view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("签到").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        //EditText edit_second = (EditText) textEntryView.findViewById(R.id.edit_second);
                        final String operId = edit_first.getText().toString();
                        //final String appId = edit_second.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        if (operId == null || operId.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入操作员号", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "LOGON");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("operId", operId);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnLogout() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("签退").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("transType", "LOGOFF");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnSettle() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("结算").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("transType", "SETTLE");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnPrnLast() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("打印最后一笔").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("transType", "PRN_LAST");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnPrnDetail() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("打印交易明细").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("transType", "PRN_DETAIL");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnPrnTotal() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("打印交易汇总").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("transType", "PRN_TOTAL");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    Log.i("Test", "0000");
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");
                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnPrnLastBatch() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("打印上批总计").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("transType", "PRN_LAST_BATCH");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnPrintAny() {

        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("请输流水号或平台订单号");
        //view_second.setText("请输入订单号");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("重打任意一笔").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        //EditText edit_second = (EditText) textEntryView.findViewById(R.id.edit_second);

                        final String voucher = edit_first.getText().toString();
                        //final String orderId = edit_second.getText().toString();


//                        if (orderId == null || orderId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入订单号", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "PRN_ANY");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    if (voucher == null || voucher.length() == 0)
                                    {
                                        //不送
                                    }
                                    else
                                        json.put("voucherNo",  voucher);

//                                    if (orderId == null || orderId.length() == 0)
//                                    {
//                                        //不送
//                                    }
//                                    else
//                                        json.put("orderNo",  orderId);

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnReadCradNo() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("读卡号").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "GET_CARD_NO");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnSetting() {

        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("终端参数设置").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "SETTING");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void prnResult(final String result) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                resultTv.setText(Util.parseResp(result));
            }
        });

    }

    private void btnBitmap() {

        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_one, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);

        view_first.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("打印图片").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.i("Test", "start onClick");
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String appId = edit_first.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        final ProgressDialog dialog2 = ProgressDialog.show(MainActivity.this, "", "图片生成中", false);
                        dialog2.show();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("transType", "PRN_BITMAP");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("bitmap", bitmaptoString(createBitmap()));
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");
                                    dialog2.dismiss();
                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnQuery() {

        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("请输流水号或平台订单号");
        //view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("交易查询").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String orderNo = edit_first.getText().toString();

//                        EditText edit_second = (EditText) textEntryView.findViewById(R.id.edit_second);
//                        final String appId = edit_second.getText().toString();

//                        if (orderNo == null || orderNo.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入订单号", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "TRANS_QUERY");
                                    json.put("appId", CConfig.APPIDNAME);//appId//
                                    if (orderNo != null && orderNo.length()>0)
                                        json.put("voucherNo", orderNo);//

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnQrSale() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("交易金额");
        //view_second.setText("应用ID");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("扫码消费").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        final String transAmount = edit_first.getText().toString();
                        //EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        //final String appId = edit_seconed.getText().toString();

                        if (transAmount == null || transAmount.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "QR_SALE");
                                    json.put("transAmount", transAmount);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnQrVoid() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_double, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        //TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);

        view_first.setText("付款凭证号(扫码)");
        //view_second.setText("应用ID");


        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("扫码撤销").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        //EditText edit_second = (EditText) textEntryView.findViewById(R.id.edit_second);

                        final String origC2bVoucherNo = edit_first.getText().toString();
                        //final String appId = edit_second.getText().toString();

                        if (origC2bVoucherNo == null || origC2bVoucherNo.length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入付款凭证号", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "QR_VOID");
                                    json.put("origC2bVoucherNo", origC2bVoucherNo);
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    private void btnQrRefund() {
        final View textEntryView = LayoutInflater.from(MainActivity.this).inflate(R.layout.trans_edit_triple, null);

        TextView view_first = (TextView) textEntryView.findViewById(R.id.view_first);
        TextView view_second = (TextView) textEntryView.findViewById(R.id.view_second);
        TextView view_third = (TextView) textEntryView.findViewById(R.id.view_third);

        view_first.setText("交易金额");
        view_second.setText("应用ID");
        view_third.setText("付款凭证号(扫码)");

        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("退货").setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edit_first = (EditText) textEntryView.findViewById(R.id.edit_first);
                        EditText edit_seconed = (EditText) textEntryView.findViewById(R.id.edit_second);
                        EditText edit_third = (EditText) textEntryView.findViewById(R.id.edit_third);

                        final String transAmount = edit_first.getText().toString();
                        final String appId = edit_seconed.getText().toString();
                        final String origC2bVoucherNo = edit_third.getText().toString();

//                        if (appId == null || appId.length() == 0) {
//                            Toast.makeText(MainActivity.this, "请输入应用ID", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject();

                                    json.put("transType", "QR_REFUND");
                                    json.put("appId", CConfig.APPIDNAME);//appId
                                    json.put("transAmount", transAmount);
                                    json.put("origC2bVoucherNo", origC2bVoucherNo);
                                    json.put("orderNo", SystemClock.currentThreadTimeMillis() + "");

                                    String result = payHelper.doTrans(json.toString());
                                    prnResult(result);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
        dlg.show();
    }

    public String bitmaptoString(Bitmap bitmap) {
        // 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    private Bitmap createBitmap() {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("test.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
