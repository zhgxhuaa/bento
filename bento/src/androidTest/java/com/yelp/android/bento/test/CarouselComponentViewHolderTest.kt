package com.yelp.android.bento.test

import androidx.recyclerview.widget.RecyclerView
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.CarouselComponentViewHolder
import com.yelp.android.bento.components.CarouselViewModel
import com.yelp.android.bento.components.SimpleComponent
import com.yelp.android.bento.core.ComponentGroup
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Test

class CarouselComponentViewHolderTest: ComponentViewHolderTestCase<Unit?, CarouselViewModel>() {

    @Test
    fun providingRecycledPool_SharesPool() {
        val group = ComponentGroup()
        group.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        val pool = RecyclerView.RecycledViewPool()

        bindViewHolder(CarouselComponentViewHolder::class.java, null, CarouselViewModel(group, pool))
        val holder = getHolder<CarouselComponentViewHolder>()

        assertEquals(pool, holder.recyclerView.recycledViewPool)
    }

    @Test
    fun carousel_ReceivesSharePool() {
        val context = mActivityTestRule.activity
        val recyclerView = RecyclerView(context)
        val pool = recyclerView.recycledViewPool

        val controller = RecyclerViewComponentController(recyclerView)
        val carousel = CarouselComponent()

        assertNull(carousel.getItem(0).sharedPool)
        controller.addComponent(carousel)
        assertEquals(pool, carousel.getItem(0).sharedPool)
    }

    @Test
    fun nestedCarousel_ReceivesSharePool() {
        val context = mActivityTestRule.activity
        val recyclerView = RecyclerView(context)
        val pool = recyclerView.recycledViewPool

        val controller = RecyclerViewComponentController(recyclerView)
        val carousels = (1..3).map { CarouselComponent() }

        val group = ComponentGroup().addAll(listOf(
                ComponentGroup().addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                carousels[0],
                ComponentGroup().addAll((1..10).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
                        .addComponent(carousels[1])
                        .addAll((1..5).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                ComponentGroup().addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                carousels[2]
        ))

        carousels.forEachIndexed { index, carousel ->
            assertNull("At carousel: $index", carousel.getItem(0).sharedPool)
        }
        controller.addComponent(group)
        carousels.forEachIndexed { index, carousel ->
            assertEquals("At carousel: $index", pool, carousel.getItem(0).sharedPool)
        }
    }
}