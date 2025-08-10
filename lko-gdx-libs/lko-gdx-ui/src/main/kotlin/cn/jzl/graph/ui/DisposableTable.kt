package cn.jzl.graph.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisTable

abstract class DisposableTable : VisTable() {
    private var initialized = false

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        if (stage != null && !initialized) {
            initializeWidget()
            initialized = true
        } else if (stage == null && initialized) {
            disposeWidget()
            initialized = false
        }
    }
    protected abstract fun initializeWidget()
    protected abstract fun disposeWidget()
}