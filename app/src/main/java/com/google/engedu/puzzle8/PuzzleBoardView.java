package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            // Do something.
            // Doing something :P
            int i;
            for(i=0;i<NUM_SHUFFLE_STEPS;i++)
            {
                ArrayList<PuzzleBoard> PZB_list = puzzleBoard.neighbours();
                puzzleBoard = PZB_list.get(random.nextInt(PZB_list.toArray().length));
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
        Comparator<PuzzleBoard> comparator = new PuzzleObjectComparator();

        PriorityQueue<PuzzleBoard> pq=new PriorityQueue<>(1000,comparator);
        PuzzleBoard temp=puzzleBoard;
        temp.setPreviousBoard(null);
        temp.setNumMoves(0);
        pq.add(temp);
        while(!pq.isEmpty()){
            temp=pq.remove();
            if(temp.getMHDistance()==0){
                ArrayList<PuzzleBoard> col=new ArrayList<PuzzleBoard>();
                col.add(temp);
                while (temp.getPreviousBoard()!=null){
                    col.add(temp.getPreviousBoard());
                    temp=temp.getPreviousBoard();
                }
                Collections.reverse(col);
                animation=col;
                invalidate();
                return;
            }
            ArrayList<PuzzleBoard> temp2=temp.neighbours();
            for(int i=0;i<temp2.size();i++){
                temp2.get(i).setPreviousBoard(temp);
                temp2.get(i).setNumMoves(temp.getNumMoves()+1);
                pq.add(temp2.get(i));
            }
        }
    }
}

class PuzzleObjectComparator implements Comparator<PuzzleBoard> {
    @Override
    public int compare(PuzzleBoard a,PuzzleBoard b){
        int a1=a.getNumMoves();
        int b1=b.getNumMoves();
        int a2=a.getMHDistance();
        int b2=b.getMHDistance();
        if(a1+a2<b1+b2){
            return -1;
        }else if(a1+a2>b1+b2){
            return 1;
        }else{return 0;}
    }
}

