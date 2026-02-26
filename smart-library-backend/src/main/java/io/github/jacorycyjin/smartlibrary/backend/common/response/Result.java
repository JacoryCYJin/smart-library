package io.github.jacorycyjin.smartlibrary.backend.common.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.util.BaseSerial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 结果类
 * 
 * @author Jacory
 * @date 2025/12/11
 */

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result<T> extends BaseSerial {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 构造函数
     */
    public Result() {
        this.code = ApiCode.SUCCESS.getCode();
    }

    /**
     * 构造函数
     * 
     * @param errCode
     * @param message
     */
    public Result(Integer errCode, String message) {
        this.code = errCode;
        this.message = message;
    }

    /**
     * 成功
     * 
     * @param <T>
     * @return
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ApiCode.SUCCESS.getCode());
        return result;
    }

    /**
     * 成功
     * 
     * @param <T>
     * @param data
     * @return
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ApiCode.SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    /**
     * 失败
     * 
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(ApiCode.SERVER_ERROR.getCode());
        return result;
    }

    /**
     * 失败
     * 
     * @param <T>
     * @param message
     * @return
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(ApiCode.SERVER_ERROR.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 失败
     * 
     * @param <T>
     * @param code
     * @param message
     * @return
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 是否成功
     * 
     * @return
     */
    public boolean isSuccess() {
        return code != null && code.equals(ApiCode.SUCCESS.getCode());
    }
}
