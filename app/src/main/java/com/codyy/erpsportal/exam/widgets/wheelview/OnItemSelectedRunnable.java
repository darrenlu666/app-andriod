package com.codyy.erpsportal.exam.widgets.wheelview;

final class OnItemSelectedRunnable implements Runnable {
    final LoopView loopView;

    OnItemSelectedRunnable(LoopView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        if (loopView != null && loopView.onItemSelectedListener != null) {
            loopView.onItemSelectedListener.onItemSelected(loopView.getSelectedItem());
        }
    }
}
