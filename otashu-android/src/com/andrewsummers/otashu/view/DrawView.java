
package com.andrewsummers.otashu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

/**
 * View a generated picture of a particular emotion's fingerprint.
 * <p>
 * This activity allows a user to see how an emotion's fingerprint appears.  
 * </p>
 */
public class DrawView extends View {
    Paint paint = new Paint();
    SparseArray<SparseArray<Integer>> mEmofing = new SparseArray<SparseArray<Integer>>();

    public DrawView(Context context, SparseArray<SparseArray<Integer>> emofing) {
        super(context);
        mEmofing = emofing;
    }

    @Override
    public void onDraw(Canvas canvas) {
        
        Log.d("MYLOG", "emofing received: " + mEmofing.toString());
        
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(5.0f);
        // 6. Loop through all found paths
        for (int i = 1; i <= mEmofing.size(); i++) {
            for (int j = 1; j <= 12; j++) {        
                // 7. Plot root number reductions (the emofing)
                canvas.drawPoint((float) j, (float) i, paint);
            }
        }
        
        /*
        paint.setColor(Color.BLUE);
        paint.setAlpha(51);
        canvas.drawRect(0, 0, 50, 50, paint);
        paint.setColor(Color.RED);
        paint.setAlpha(102);
        canvas.drawRect(50, 0, 100, 50, paint);
        paint.setColor(Color.YELLOW);
        paint.setAlpha(153);
        canvas.drawRect(100, 0, 150, 50, paint);
        paint.setColor(Color.GREEN);
        paint.setAlpha(204);
        canvas.drawRect(150, 0, 200, 50, paint);
        */
    }
}
