package com.example.tetris.logic.model
enum class TetrisCubeType(val color: Int) {

    /** 亮蓝 I */
    I(0xFF00BCD4.toInt()),
    /** 明黄 O */
    O(0xFFFFEB3B.toInt()),
    /** 纯紫 T */
    T(0xFF9C27B0.toInt()),
    /** 草绿 S */
    S(0xFF4CAF50.toInt()),
    /** 鲜红 Z */
    Z(0xFFF44336.toInt()),
    /** 活力橙 L */
    L(0xFFFF9800.toInt()),
    /** 蓝 J */
    J(0xFF2196F3.toInt());

    /** 包裹盒等于多大的矩阵包裹住了这个形状 */
    val boundingBoxSize: Int
        get() = when (this) {
            I -> 4
            O -> 2
            else -> 3
        }

    companion object {
        /**
         * 得到某个形状方块在指定旋转角度下的填充格子偏移列表。
         */
        fun getShapeOffsets(type: TetrisCubeType, rotation: Int): List<Pair<Int, Int>> {
            val index = rotation and 3 // 确保 rotation ∈ [0, 3]
            return SHAPES[type]?.getOrNull(index) ?: emptyList()
        }

        // region ---------- 形状定义 ----------

        // I  piece — 4×4 包围盒
        private val I_SHAPES = listOf(
            listOf(1 to 0, 1 to 1, 1 to 2, 1 to 3),  // R0  ─ 横向
            listOf(0 to 2, 1 to 2, 2 to 2, 3 to 2),  // R1  │ 竖向
            listOf(2 to 0, 2 to 1, 2 to 2, 2 to 3),  // R2  ─ 横向（反向）
            listOf(0 to 1, 1 to 1, 2 to 1, 3 to 1),  // R3  │ 竖向（反向）
        )

        // O  piece — 2×2 包围盒，所有旋转不变
        private val O_SHAPES = listOf(
            listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
            listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
            listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
            listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
        )

        // T  piece — 3×3 包围盒
        // R0: .X.   R1: .X.   R2: ...   R3: .X.
        //     XXX       .XX       XXX       XX.
        //     ...       .X.       .X.       .X.
        private val T_SHAPES = listOf(
            listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2),
            listOf(0 to 1, 1 to 1, 1 to 2, 2 to 1),
            listOf(1 to 0, 1 to 1, 1 to 2, 2 to 1),
            listOf(0 to 1, 1 to 0, 1 to 1, 2 to 1),
        )

        // S  piece — 3×3 包围盒
        // R0: .XX   R1: .X.   R2: ...   R3: X..
        //     XX.       .XX       .XX       XX.
        //     ...       ..X       XX.       .X.
        //
        // 注意：标准 SRS 的 S 方块顺序应为：
        // R1 = CW from R0 = (.X./.XX/..X)
        // R3 = CCW from R0 = (X../XX./.X.)
        private val S_SHAPES = listOf(
            listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1),
            listOf(0 to 1, 1 to 1, 1 to 2, 2 to 2),
            listOf(1 to 1, 1 to 2, 2 to 0, 2 to 1),
            listOf(0 to 0, 1 to 0, 1 to 1, 2 to 1),
        )

        // Z  piece — 3×3 包围盒
        // R0: XX.   R1: ..X   R2: ...   R3: X..
        //     .XX       .XX       XX.       XX.
        //     ...       .X.       .XX       .X.
        private val Z_SHAPES = listOf(
            listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2),
            listOf(0 to 2, 1 to 1, 1 to 2, 2 to 1),
            listOf(1 to 0, 1 to 1, 2 to 1, 2 to 2),
            listOf(0 to 1, 1 to 0, 1 to 1, 2 to 0),
        )

        // L  piece — 3×3 包围盒
        // R0: X..   R1: .XX   R2: ...   R3: .X.
        //     XXX       .X.       XXX       .X.
        //     ...       .X.       ..X       XX.
        private val L_SHAPES = listOf(
            listOf(0 to 0, 1 to 0, 1 to 1, 1 to 2),
            listOf(0 to 1, 0 to 2, 1 to 1, 2 to 1),
            listOf(1 to 0, 1 to 1, 1 to 2, 2 to 2),
            listOf(0 to 1, 1 to 1, 2 to 0, 2 to 1),
        )

        // J  piece — 3×3 包围盒
        // R0: ..X   R1: .X.   R2: ...   R3: XX.
        //     XXX       .X.       XXX       .X.
        //     ...       .XX       X..       .X.
        private val J_SHAPES = listOf(
            listOf(0 to 2, 1 to 0, 1 to 1, 1 to 2),
            listOf(0 to 1, 1 to 1, 2 to 1, 2 to 2),
            listOf(1 to 0, 1 to 1, 1 to 2, 2 to 0),
            listOf(0 to 0, 0 to 1, 1 to 1, 2 to 1),
        )

        private val SHAPES: Map<TetrisCubeType, List<List<Pair<Int, Int>>>> = mapOf(
            I to I_SHAPES,
            O to O_SHAPES,
            T to T_SHAPES,
            S to S_SHAPES,
            Z to Z_SHAPES,
            L to L_SHAPES,
            J to J_SHAPES,
        )

        // endregion
    }
}