package com.example.demo.recommend.core;

import com.example.demo.recommend.dto.RelateDTO;
import com.google.common.collect.Lists;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 核心算法
 */
public class CoreMath {
    /**
     * 推荐
     * @param userId 用户id
     * @param list 推荐的idList集合
     * @return
     */
    public List<Integer> recommend(Integer userId, List<RelateDTO> list) {
        if (userId == null || list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 找到最近邻用户id
        Map<Double, Integer> distances = computeNearestNeighbor(userId, list);
        if (distances == null || distances.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取相似度最高的用户id
        Double minDistance = Collections.min(distances.keySet());
        Integer nearest = distances.get(minDistance);

        // 构建用户购买记录映射
        Map<Integer, List<RelateDTO>> userMap = list.stream()
                .collect(Collectors.groupingBy(RelateDTO::getUserId));

        // 获取最近邻用户和指定用户的购买记录
        List<Integer> neighborItemList = getUserItemIds(userMap, nearest);
        List<Integer> userItemList = getUserItemIds(userMap, userId);

        // 找到最近邻买过但该用户没买过的商品id，计算推荐，放入推荐列表
        Set<Integer> userItemSet = new HashSet<>(userItemList);
        List<Integer> recommendList = neighborItemList.stream()
                .filter(item -> !userItemSet.contains(item))
                .sorted()
                .collect(Collectors.toList());

        return recommendList;
    }

    /**
     * 获取指定用户的购买商品id列表
     *
     * @param userMap 用户购买记录映射
     * @param userId  用户id
     * @return 商品id列表
     */
    private List<Integer> getUserItemIds(Map<Integer, List<RelateDTO>> userMap, Integer userId) {
        if (userId == null || !userMap.containsKey(userId)) {
            return Collections.emptyList();
        }
        return userMap.get(userId).stream()
                .map(RelateDTO::getProductId)
                .collect(Collectors.toList());
    }

    /**
     * 在给定userId的情况下，计算其他用户和它的相关系数并排序
     *
     * @param userId 用户id
     * @param list   推荐的idList集合
     * @return 相关系数与用户id的映射
     */
    private Map<Double, Integer> computeNearestNeighbor(Integer userId, List<RelateDTO> list) {
        Map<Integer, List<RelateDTO>> userMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getUserId));
        Map<Double, Integer> distances = new TreeMap<>();
        userMap.forEach((k, v) -> {
            if (!k.equals(userId)) {
                double distance = pearson_dis(v, userMap.get(userId));
                distances.put(distance, k);
            }
        });
        return distances;
    }

    /**
     * 计算两个序列间的相关系数
     *
     * @param xList 其他用户的数据集
     * @param yList 当前用户的数据集
     * @return
     */
    private double pearson_dis(List<RelateDTO> xList, List<RelateDTO> yList) {
        List<Integer> xs= Lists.newArrayList();
        List<Integer> ys= Lists.newArrayList();
        xList.forEach(x->{
            yList.forEach(y->{
                if(x.getProductId().intValue() == y.getProductId().intValue()){
                    xs.add(x.getIndex());
                    ys.add(y.getIndex());
                }
            });
        });
        return getRelate(xs,ys);
    }

    /**
     * 方法描述: 皮尔森（pearson）相关系数计算
     * (x1,y1) 理解为 a 用户对 x 商品的点击次数和对 y 商品的点击次数
     * @param xs
     * @param ys
     * @Return {@link Double}
     * @throws
     */
    public static Double getRelate(List<Integer> xs, List<Integer> ys){
        int n=xs.size();
        double Ex= xs.stream().mapToDouble(x->x).sum();
        double Ey=ys.stream().mapToDouble(y->y).sum();
        double Ex2=xs.stream().mapToDouble(x-> Math.pow(x,2)).sum();
        double Ey2=ys.stream().mapToDouble(y-> Math.pow(y,2)).sum();
        double Exy= IntStream.range(0,n).mapToDouble(i->xs.get(i)*ys.get(i)).sum();
        double numerator=Exy-Ex*Ey/n;
        double denominator= Math.sqrt((Ex2- Math.pow(Ex,2)/n)*(Ey2- Math.pow(Ey,2)/n));
        if (denominator==0) {
            return 0.0;
        }
        return numerator/denominator;
    }

}
