package com.wang17.myclock.utils;

import android.util.Log;

import com.wang17.myclock.database.Stock;
import com.wang17.myclock.model.Commodity;
import com.wang17.myclock.model.StockInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class _SinaStockUtils {

    private static String parseItem(String code) {
        StringBuffer item = new StringBuffer();
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (!Character.isDigit(c)) {
                item.append(c);
            } else {
                break;
            }
        }
        return item.toString();
    }

    public static Commodity findCommodity(String code) {
        for (Commodity commodity : _Session.commoditys) {
            if (commodity.item.toLowerCase().equals(parseItem(code).toLowerCase())) {
                return commodity;
            }
        }
        return null;
    }

    public static void getStockInfoList(final List<Stock> stocks, final OnLoadStockInfoListListener onLoadStockInfoListListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isStock = true;
                double totalProfit = 0;
                int totalAmount = 0;
                String time = "";
                List<StockInfo> stockInfoList = new ArrayList<>();
                try {
                    for (Stock stock : stocks) {
//                        final Stock stock = stock;
                        String url = "https://hq.sinajs.cn/list=" + stock.getExchange() + stock.getCode();

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String body = response.body().string();
                            String[] result = body.substring(body.indexOf("\"")).replace("\"", "").split(",");

                            StockInfo info = new StockInfo();
                            double profit = 0;
                            if (stock.getType() == 0) {
                                /**
                                 * 股票列表
                                 */
                                isStock = true;
                                double open = Double.parseDouble(result[2]);
                                info.name = result[0];
                                info.price = Double.parseDouble(result[3]);
                                info.increase = (info.price - open) / open;

                                info.time = result[31];
                                profit = (info.price - stock.getCost()) / stock.getCost();
                                totalProfit += profit * stock.getAmount() * stock.getCost() * 100;
                                totalAmount += stock.getAmount() * stock.getCost() * 100;
                            } else {
                                /**
                                 * 期货列表
                                 */
                                isStock = false;
                                double open = Double.parseDouble(result[2]);
                                info.name = result[0];
                                info.price = Double.parseDouble(result[8]);
                                double yesterdayClose = Double.parseDouble(result[5]);
                                info.increase = info.price - yesterdayClose;
                                info.time = result[1];
                                Commodity commodity = findCommodity(stock.getCode());
                                profit = stock.getType() * (info.price - stock.getCost()) * stock.getAmount() * commodity.unit;
                                totalProfit+=profit;
                            }
                            time = info.time;
                            info.type = stock.getType();
                            info.code = stock.getCode();
                            info.cost = stock.getCost();
                            info.exchange = stock.getExchange();
                            info.amount = stock.getAmount();


                            stockInfoList.add(info);
                        } else {
                            _Utils.runlog2file("获取数据失败...");
                            return;
                        }
                    }

                    double averageProfit = 0;

                    if (isStock) {
                        averageProfit = totalProfit / totalAmount;
                    } else {
                        averageProfit = totalProfit;
                    }
                    if (onLoadStockInfoListListener != null) {
                        if (stocks.size() != 0)
                            onLoadStockInfoListListener.onLoadFinished(stockInfoList, totalProfit, averageProfit, time);
                        else
                            onLoadStockInfoListListener.onLoadFinished(stockInfoList, 0, 0, time);
                    }
//                    return stockInfoList;
                } catch (Exception e) {
                    _Utils.error2file("SinaStockUtils.getStockInfoList  error: ",e.getMessage());
                    Log.e("wangsc", e.getMessage());
                }
            }
        }).start();
    }

    public static void getStockInfo(final StockInfo info, final OnLoadStockInfoListener onLoadStockInfoListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String time = "";

                try {
                    String url = "https://hq.sinajs.cn/list=" + info.exchange + info.code;

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        String[] result = body.substring(body.indexOf("\"")).replace("\"", "").split(",");

                        double open = Double.parseDouble(result[2]);
                        info.name = result[0];
                        info.price = Double.parseDouble(result[3]);
                        info.increase = (info.price - open) / open;
                        info.time = result[31];
                        time = info.time;
                    }


                    if (onLoadStockInfoListener != null) {
                        onLoadStockInfoListener.onLoadFinished(info, time);
                    }
//                    return stockInfoList;
                } catch (Exception e) {
                    _Utils.error2file("SinaStockUtils.getStockInfo  error: ",e.getMessage());
                    Log.e("wangsc", e.getMessage());
                }
            }
        }).start();
    }

    public interface OnLoadStockInfoListener {
        void onLoadFinished(StockInfo infoList, String time);
    }

    public interface OnLoadStockInfoListListener {
        void onLoadFinished(List<StockInfo> infoList, double totalProfit, double averageProfit, String time);
    }
}
