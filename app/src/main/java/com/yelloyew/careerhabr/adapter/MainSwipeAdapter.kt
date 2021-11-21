package com.yelloyew.careerhabr.adapter

import android.graphics.Canvas
import android.util.Log
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.ui.MainFragment
import kotlin.math.absoluteValue

class MainSwipeAdapter(private var adapter: MainFragment.MainRecyclerAdapter) :
    ItemTouchHelper.Callback() {

    private var lastAlpha = 0

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return .28f
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.likeVacancy(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        try {
            val position = viewHolder.adapterPosition
            val v = recyclerView.layoutManager!!.findViewByPosition(position)

            val likeImageView: ImageView = v!!.findViewById(R.id.like_imageview)
            val likeBackground: ImageView = v.findViewById(R.id.like_background_imageview)

            val alpha = (dX.absoluteValue / 10).toInt()
            Log.d("tag", alpha.toString())

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                if (alpha in 4..107) {
                    likeImageView.isVisible = true
                    likeBackground.isVisible = true
                    if (alpha < 28) {
                        likeImageView.imageAlpha = alpha * 9
                        likeBackground.imageAlpha = alpha * 9
                    }
                    lastAlpha = alpha
                }
                else {
                    likeImageView.imageAlpha = 0
                    likeBackground.imageAlpha = 0
                }
            }
        }
        catch (e: Exception){
            Log.d("tagMainSwipe", e.toString())
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}