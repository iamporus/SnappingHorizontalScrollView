/*
 * Copyright (C) 2014 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roomies.snappinghorizontalscrollview;

/**
 * @author Purushottam Pawar
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Extends the default HorizontalScrollView of android to provide snapping
 * ability to child items. When scrolled to either of the directions, child
 * items snap to the fixed positions and the item at center position is
 * displayed as selected. Clicking on child items works in same fashion.
 */
public class SnappingHorizontalScrollView extends HorizontalScrollView {

	/**
	 * Callback when snapping is finished and one of the child item is selected.
	 */
	public interface OnScrollerItemSelectedListener {
		public void onScrollerItemSelected(int selectedItem);
	}

	/**
	 * Custom layout to lay out the children at equal distances
	 */
	private EquiSpacerLayout internalWrapper;

	/**
	 * currently selected child item
	 */
	private int selectedItem;

	/**
	 * listener to provide callback on item selected upon scroll or click
	 */
	private OnScrollerItemSelectedListener mItemSelectedListener;

	/**
	 * minimum allowed child width
	 */

	protected int childWidth = EquiSpacerLayout.MIN_CHILD_WIDTH;

	public SnappingHorizontalScrollView(Context context) {
		super(context);
		init(context, null);
	}

	public SnappingHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SnappingHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		internalWrapper = new EquiSpacerLayout(getContext());
		internalWrapper.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
		addView(internalWrapper);

		setOnTouchListener(mTouchListener);
	}

	/**
	 * Get index of currently selected item
	 * 
	 * @return index of currently selected item
	 */
	public int getSelectedItem() {
		return selectedItem;
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	/**
	 * Set an item as selected. The list is scrolled to the index of selected
	 * item and the item is highlighted.
	 * 
	 * @param isClicked
	 *            pass in true if the item is clicked else false
	 * @param selectedItem
	 *            the index of item that should be selected
	 */
	private void setSelectedItem(boolean isClicked, int selectedItem) {

		int currentSelection = selectedItem;
		int visibleItems = internalWrapper.getVisibleChildrenCount();
		if (!isClicked)
			currentSelection = selectedItem + visibleItems / 2;

		if (internalWrapper.getChildAt(currentSelection) instanceof TextView) {

			((TextView) internalWrapper.getChildAt(currentSelection))
					.setTextColor(Color.RED);
			for (int i = 0; i < internalWrapper.getChildCount(); i++) {
				if (i != currentSelection)
					((TextView) internalWrapper.getChildAt(i))
							.setTextColor(Color.GRAY);
			}
		}

		if (isClicked) {
			smoothScrollTo(
					(selectedItem - visibleItems / 2) * (int) childWidth, 0);
			this.selectedItem = selectedItem - visibleItems / 2;
		} else {
			smoothScrollTo(selectedItem * (int) childWidth, 0);
			this.selectedItem = selectedItem;
		}

		internalWrapper.invalidate();

		if (mItemSelectedListener != null)
			mItemSelectedListener.onScrollerItemSelected(getSelectedItem());
	}

	/**
	 * Set an item as selected. The list is scrolled to the index of selected
	 * item and the item is highlighted.
	 * 
	 * @param selectedItem
	 *            the index of item that should be selected
	 */
	public void setSelectedItem(int index) {
		setSelectedItem(false, index);
	}

	/**
	 * Register a callback to be invoked when an item is selected from the
	 * horizontal list
	 * 
	 * @param mListener
	 *            The callback that will run
	 */
	public void setOnScrollerItemSelectedListener(
			OnScrollerItemSelectedListener mListener) {
		this.mItemSelectedListener = mListener;
	}

	/**
	 * Set String items as child views to the SnappingHorizontalScrollView.
	 * 
	 * @param items
	 *            Array of items to be displayed as child inside
	 *            SnappingHorizontalScrollView
	 */
	public void setFeatureItems(ArrayList<String> items) {

		for (int i = 0; i < items.size(); i++) {

			TextView tx = (TextView) View.inflate(getContext(),
					R.layout.list_item, null);
			tx.setText(items.get(i));
			tx.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					for (int i = 0; i < internalWrapper.getChildCount(); i++) {
						if (internalWrapper.getChildAt(i) == v)
							setSelectedItem(true, i);
					}
				}
			});
			internalWrapper.addView(tx);
		}

		addDummyItems();

	}

	/**
	 * Set inflated custom layouts as children to the
	 * SnappingHorizontalScrollView.
	 * 
	 * @param items
	 *            Array of items to be displayed as child inside
	 *            SnappingHorizontalScrollView
	 */
	public void setFeatureItems(View[] items) {

		removeDummyItems();
		for (int i = 0; i < items.length; i++) {
			View item = items[i];
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					for (int i = 0; i < internalWrapper.getChildCount(); i++) {
						if (internalWrapper.getChildAt(i) == v)
							setSelectedItem(true, i);
					}
				}
			});
			internalWrapper.addView(item);
		}

		addDummyItems();
	}

	/**
	 * Add a single item at the end of the list
	 * 
	 * @param item
	 *            view to be added in the list
	 */
	public void addFeatureItem(View item) {

		removeDummyItems();
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				for (int i = 0; i < internalWrapper.getChildCount(); i++) {
					if (internalWrapper.getChildAt(i) == v)
						setSelectedItem(true, i);
				}
			}
		});

		internalWrapper.addView(item);

		addDummyItems();

	}

	/**
	 * Add dummy items in the list so as to bring the selected item at center
	 * position
	 */
	private void addDummyItems() {

		/*-  	add dummy items at the start of scroller		-*/

		for (int i = 0; i < internalWrapper.getVisibleChildrenCount() / 2; i++) {
			TextView tx = (TextView) View.inflate(getContext(),
					R.layout.list_item, null);
			tx.setText("");
			tx.setTag("dummy");
			internalWrapper.addView(tx, i);
		}

		/*- 	add dummy items at the end of scroller  	-*/

		for (int i = 0; i < internalWrapper.getVisibleChildrenCount() / 2 - 1; i++) {
			TextView tx = (TextView) View.inflate(getContext(),
					R.layout.list_item, null);
			tx.setText("");
			internalWrapper.addView(tx, internalWrapper.getChildCount());
		}
	}

	/**
	 * removes dummy items from the list
	 */
	private void removeDummyItems() {

		for (int i = 0; i < internalWrapper.getChildCount(); i++) {

			if (internalWrapper.getChildAt(i).getTag() != null)
				if (internalWrapper.getChildAt(i).getTag().toString()
						.contentEquals("dummy"))
					internalWrapper.removeViewAt(i);
		}

	}

	private OnTouchListener mTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			childWidth = internalWrapper.getChildAt(0).getWidth();
			if (event.getAction() == MotionEvent.ACTION_UP
					|| event.getAction() == MotionEvent.ACTION_CANCEL) {
				int scrollX = getScrollX();
				int factor = scrollX / (int) childWidth;

				if (scrollX < childWidth / 2) {
					factor = 0;
				} else {
					if (factor == 0
							|| scrollX > (factor * childWidth) + childWidth / 2) {
						factor++;
					}
				}

				setSelectedItem(false, factor);

				return true;
			} else {

				return false;
			}
		}
	};

	/**
	 * Aim of this layout is to lay out its children with fixed widths and
	 * heights spaced out at equal distance
	 * 
	 * @author Purushottam Pawar
	 * 
	 */
	private class EquiSpacerLayout extends LinearLayout {

		private static final int MIN_CHILD_WIDTH = 100;
		private int maxWidth;
		private int calculatedChildWidth = MIN_CHILD_WIDTH;

		public EquiSpacerLayout(Context context) {
			super(context);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			((Activity) getContext()).getWindowManager().getDefaultDisplay()
					.getMetrics(displaymetrics);
			maxWidth = displaymetrics.widthPixels;
		}

		public int getVisibleChildrenCount() {

			return maxWidth / calculatedChildWidth;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

			/*-
			
			1. Get proposed width for the parent using received measure specs
			2. Calculate child width which shall be	totalParentWidth/noOfChildren
			3. Create Measure Specs for child using derived child width
			4. Get height of child with max height
			5. Create Measure Specs for the height using derived child height
			6. Pass the specs to every child by calling measure() on every child
			7. call setMeasuredDimensions on parent with original received width
			and max height of child
			
			-*/

			int numberOfChilds = getChildCount();

			int width = MeasureSpec.getSize(widthMeasureSpec)
					- getPaddingLeft() - getPaddingRight();

			int height = MeasureSpec.getSize(heightMeasureSpec)
					- getPaddingBottom() - getPaddingTop();

			calculatedChildWidth = Math.max(
					(maxWidth - getPaddingLeft() - getPaddingRight())
							/ numberOfChilds, MIN_CHILD_WIDTH);

			int heightMode = MeasureSpec.getMode(heightMeasureSpec);

			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
					calculatedChildWidth, MeasureSpec.EXACTLY);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
					heightMode);

			int maxChildHeight = 0;
			for (int i = 0; i < numberOfChilds; i++) {

				View child = getChildAt(i);
				child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
				width += child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();
				if (childHeight > maxChildHeight)
					maxChildHeight = childHeight;

			}

			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
					maxChildHeight, MeasureSpec.EXACTLY);

			for (int i = 0; i < numberOfChilds; i++) {
				getChildAt(i).measure(childWidthMeasureSpec,
						childHeightMeasureSpec);
			}

			setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(),
					maxChildHeight + getPaddingBottom() + getPaddingTop());

		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			int childCount = getChildCount();

			int childTop = getPaddingTop();

			for (int i = 0; i < childCount; i++) {

				View child = getChildAt(i);
				final int childMeasuredWidth = child.getMeasuredWidth();
				final int childMeasuredHeight = child.getMeasuredHeight();

				int childLeft = (i * childMeasuredWidth) + getPaddingLeft();
				child.layout(childLeft, childTop, childLeft
						+ childMeasuredWidth, childTop + childMeasuredHeight);
			}
		}

	}

}
