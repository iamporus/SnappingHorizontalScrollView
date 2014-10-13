package com.example.snappedscrollingsample;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.roomies.snappinghorizontalscrollview.SnappingHorizontalScrollView;
import com.roomies.snappinghorizontalscrollview.SnappingHorizontalScrollView.OnScrollerItemSelectedListener;

public class MainActivity extends Activity implements
		OnScrollerItemSelectedListener {

	private SnappingHorizontalScrollView scroller;
	private TextView selectedMonthTv;
	private String[] monthsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scroller = (SnappingHorizontalScrollView) findViewById(R.id.scroller);
		selectedMonthTv = (TextView) findViewById(R.id.selectedText);
		selectedMonthTv.setTypeface(Typeface.createFromAsset(getAssets(),
				"Roboto-Thin.ttf"));

		monthsArray = getResources().getStringArray(R.array.months);

		TextView[] views = new TextView[monthsArray.length];
		for (int j = 0; j < 1; j++)
			for (int i = 0; i < monthsArray.length; i++) {
				TextView text = (TextView) View.inflate(this,
						R.layout.list_item, null);
				text.setText(monthsArray[i]);
				views[i] = text;
			}

		scroller.setFeatureItems(views);
		scroller.setOnScrollerItemSelectedListener(this);

		if (savedInstanceState != null) {
			final int selection = savedInstanceState.getInt("selectedPosition");
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					scroller.setSelectedItem(selection);
					selectedMonthTv
							.setText(selection > monthsArray.length ? monthsArray[selection
									% monthsArray.length]
									: monthsArray[selection]);
				}
			}, 200);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("selectedPosition", scroller.getSelectedItem());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onScrollerItemSelected(int selectedItem) {

		selectedMonthTv
				.setText(selectedItem > monthsArray.length ? monthsArray[selectedItem
						% monthsArray.length]
						: monthsArray[selectedItem]);
	}

}
