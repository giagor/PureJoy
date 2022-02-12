package com.topview.purejoy.common.music.player.impl

interface OperatorCallback {
    /**
     * @param operator 对数据集的操作
     * @param code 操作码 [SUCCESS_CODE]为操作成功 [FAIL_CODE]为操作失败
     * @param success 操作成功的数据，当operator为[Operator.ADD]、[Operator.REMOVE]时，类型为[Wrapper]
     * 当operator为[Operator.CLEAR]、[Operator.CLEAR]时，类型为[MutableList<Wrapper>]
     * @param fail 操作失败的数据，当operator为[Operator.ADD]、[Operator.REMOVE]时，类型为[Wrapper]
     * 当operator为[Operator.CLEAR]、[Operator.CLEAR]时，类型为[MutableList<Wrapper>]
     */
    fun callback(operator: Operator, code: Int, success: Any?, fail: Any?)

    companion object {
        const val FAIL_CODE = 0
        const val SUCCESS_CODE = 1
    }
}