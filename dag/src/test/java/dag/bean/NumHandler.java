package dag.bean;

import dag.handlers.Handler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 对于handler执行顺序的描述，用来生成有向无环图
 *
 * @author xijiu
 * @since 2022/3/31 下午8:40
 */
@Getter
@Setter
@AllArgsConstructor
public class NumHandler {
    /** 执行器序列号 */
    private int exeNum;

    /** 前执行器序列号 */
    private int[] prevExeNum;

    /** 执行器 */
    private Handler handler;

    public static NumHandler of(int exeNum, Handler handler, int[] prevExeNum) {
        return new NumHandler(exeNum, prevExeNum, handler);
    }

    public boolean isRoot() {
        return prevExeNum != null && prevExeNum.length == 1 && prevExeNum[0] == -1;
    }

    public boolean afterTargetHandler(int prevNum) {
        for (int num : prevExeNum) {
            if (Objects.equals(num, prevNum)) {
                return true;
            }
        }
        return false;
    }
}
