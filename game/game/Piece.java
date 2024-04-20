
package game;

import java.util.LinkedList;

public class Piece {

    int xPos;
    int yPos;
    int i;
    int j;

    boolean isBlack;
    LinkedList<Piece> pieceList;
    String name;
	public boolean isCaptured;
    public Piece(int xPos, int yPos, boolean isBlack,String n, LinkedList<Piece> pieceList) {
        this.xPos = xPos;
        this.yPos = yPos;
        i=xPos*64;
        j=yPos*64;
        this.isBlack = isBlack;
        this.pieceList=pieceList;
        name=n;
        pieceList.add(this);
    }
    
    //Move piece to new position if empty or after capturing
    public void move(int xPos, int yPos) {
    if (ChessGame.getPiece(xPos * 64, yPos * 64) != null) {
        if (ChessGame.getPiece(xPos * 64, yPos * 64).isBlack != isBlack) {
            ChessGame.getPiece(xPos * 64, yPos * 64).kill();

        } else {
            resetPosition();
            return;
        }
    }
    this.xPos = xPos;
    this.yPos = yPos;
    i = xPos * 64;
    j = yPos * 64;
}

//Reset Position if invalid
public void resetPosition() {
    i = xPos * 64;
    j = yPos * 64;
}

//Capturing the pieces
public void kill() {
    pieceList.remove(this);
}
//Promote pawn
public void promotePawn(String newPiece) {
    Piece promotedPiece;
    if (isBlack) {
        promotedPiece = new Piece(xPos, yPos, isBlack, newPiece, pieceList);
    } else {
        promotedPiece = new Piece(xPos, yPos, isBlack, newPiece, pieceList);
    }
    pieceList.remove(this);
}

//Check color of piece
public boolean isBlack() {
    return false;
}

public boolean PieceMovement(int xPos2, int yPos2) {
    return false;
}


}

