package com.yelloyew.careerhabr.adapter

import android.graphics.Canvas
import android.util.Log
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.ui.MainFragment

class MainSwipeAdapter(private var adapter: MainFragment.MainRecyclerAdapter) :
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeFlag(ACTION_STATE_SWIPE, LEFT)
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
        var rdX = 0f
        try {
            rdX = if (dX < -281f) {
                -281f
            } else {
                dX
            }
            val position = viewHolder.adapterPosition
            val v = recyclerView.layoutManager!!.findViewByPosition(position)

            val likeImageView: ImageView = v!!.findViewById(R.id.like_imageview)
            likeImageView.isVisible = true
            val likeBackground: ImageView = v.findViewById(R.id.like_background_imageview)
            likeBackground.isVisible = true

            if (rdX in -281f..-4f && isCurrentlyActive) {
                likeBackground.alpha = rdX / -140f
                likeImageView.alpha = rdX / -140f
            } else {
                likeBackground.alpha = 0f
                likeImageView.alpha = 0f
            }
        } catch (e: Exception) {
            Log.d("tagMainSwipe", e.toString())
        }
        super.onChildDraw(c, recyclerView, viewHolder, rdX, dY, actionState, isCurrentlyActive)
    }
}