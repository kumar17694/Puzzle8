package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private int num_moves=0;
    private PuzzleBoard previousboard;
    public void setPreviousBoard(PuzzleBoard a){previousboard=a;}
    public PuzzleBoard getPreviousBoard(){return previousboard;}
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;

    public int getMHDistance(){
        int sum=0;
        for(int i=0;i<NUM_TILES*NUM_TILES;i++){
            if(tiles.get(i)==null){
                continue;
            }
            int ax=i%NUM_TILES;
            int ay=i/NUM_TILES;
            int bx=tiles.get(i).getNumber()%NUM_TILES;
            int by=tiles.get(i).getNumber()/NUM_TILES;
            sum+=Math.abs(ax-bx);
            sum+=Math.abs(ay-by);
        }
        return sum;
    }
    public void setNumMoves(int a){num_moves=a;}
    public int getNumMoves(){return num_moves;}

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
//        tiles = new ArrayList<PuzzleTile>();
//        bitmap=Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
//        int tile_dim=parentWidth/NUM_TILES;
//        for(int i=1;i<=NUM_TILES;i++)
//            for(int j=1;j<=NUM_TILES;j++)
//            {
//                if(j==i && j==NUM_TILES)
//                {
//                    //last time
//                    tiles.add(null);
//                }
//                else
//                {
//                    //creating tiles of the image
//                    Bitmap bm = Bitmap.createBitmap(bitmap,(j-1)*tile_dim,(i-1)*tile_dim,tile_dim,tile_dim);
//                    PuzzleTile pt=new PuzzleTile(bm,NUM_TILES*(i-1) + j);
//                    tiles.add(pt);//adding tiles to the ArrayList
//
//                }
//            }
        tiles = new ArrayList<PuzzleTile>();
        bitmap=Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
        int tile_dimension=parentWidth/NUM_TILES;
        for(int i=0;i<NUM_TILES;i++)
            for(int j=0;j<NUM_TILES;j++)
            {
                if(j==i && j==NUM_TILES-1)
                {
                    //leave it empty (the last tile)
                    tiles.add(null);
                }
                else
                {
                    //creating tiles of the image
                    Bitmap tmp_img = Bitmap.createBitmap(bitmap,j*tile_dimension,i*tile_dimension,tile_dimension,tile_dimension);
                    PuzzleTile tmp_pt=new PuzzleTile(tmp_img,NUM_TILES*i + j);
                    tiles.add(tmp_pt);//adding tiles to the ArrayList

                }
            }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();

    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        //return null;
        ArrayList<PuzzleBoard> Neighbours_PZB = new ArrayList<PuzzleBoard>();
        for (int i = 0; i < NUM_TILES * NUM_TILES ; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null)
            {
                int nullX=i % NUM_TILES, nullY=i / NUM_TILES;
                for (int[] delta : NEIGHBOUR_COORDS) {
                    int tileX = nullX + delta[0];
                    int tileY = nullY + delta[1];
                    if (tileX >= 0 && tileX < NUM_TILES && tileY >= 0 && tileY < NUM_TILES) {
                        //it is a valid move
                        PuzzleBoard valid_PZB = new PuzzleBoard(this);
                        valid_PZB.swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                        Neighbours_PZB.add(valid_PZB);
                    }
                }
                break;
            }
        }

        return Neighbours_PZB;
    }

    public int priority() {
        return 0;
    }

}
