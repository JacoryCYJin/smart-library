package io.github.jacorycyjin.smartlibrary.backend.common.util;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PageUtils {

    public static int computeLastPageNumber(int totalElements, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        int result = totalElements % pageSize == 0 ? totalElements / pageSize
                : totalElements / pageSize + 1;

        if (result <= 1) {
            result = 1;
        }
        return result;
    }

    /**
     * 从所有元素中截取指定页码的数据
     *
     * @param totalList 列表总数
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @return 对象列表
     */
    public static <T> List<T> subPageList(List<T> totalList, int pageNum, int pageSize) {
        if (CollectionUtils.isEmpty(totalList) || pageNum <= 0 || pageSize <= 0) {
            return Collections.emptyList();
        }

        // 计算起止位置
        int totalSize = totalList.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = pageNum * pageSize;

        // 无数据
        if (totalSize <= startIndex) {
            return Collections.emptyList();
        }

        // 当前不足一页
        if (totalSize <= endIndex) {
            endIndex = totalSize;
        }

        // 新建列表对象返回
        return new ArrayList<T>(totalList.subList(startIndex, endIndex));
    }
}
