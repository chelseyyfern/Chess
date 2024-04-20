package game;

//Adding all necessary imports
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.*;

//Defining the ChessGame class
public class ChessGame {

    // Declaring the variables
    public static LinkedList<Piece> piecesList = new LinkedList<>(); // List containing the pieces
    public static Piece selectedPiece = null; // Piece that is clicked on
    public static boolean isBlackTurn = true; // Checking the current player's turn
    public static boolean isInCheckmate = false; // Checking for checkmate
    public static boolean isCheck = false; //Checking for check

    public static void main(String[] args) throws IOException {

        // Getting sub images from the main image "chess.png"'
        JFrame messageFrame = new JFrame();

        BufferedImage all = ImageIO.read(ChessGame.class.getResource("chess.png"));
        Image imgs[] = new Image[12];
        int imagePieceCount = 0;
        for (int j = 0; j < 400; j += 200) {
            for (int i = 0; i < 1200; i += 200) {
                imgs[imagePieceCount] = all.getSubimage(i, j, 200, 200).getScaledInstance(64, 64,
                        BufferedImage.SCALE_SMOOTH);
                imagePieceCount++;
            }
        }

        // Declaring the position of the pieces on the board
        Piece blackpiece1 = new Piece(0, 0, false, "king", piecesList);
        Piece blackpiece2 = new Piece(1, 0, false, "jumper", piecesList);
        Piece blackpiece3 = new Piece(2, 0, false, "runner", piecesList);
        Piece blackpiece4 = new Piece(3, 0, false, "tower", piecesList);
        Piece blackpiece5 = new Piece(0, 1, false, "pawn", piecesList);

        Piece whitepiece1 = new Piece(3, 4, true, "king", piecesList);
        Piece whitepiece2 = new Piece(2, 4, true, "jumper", piecesList);
        Piece whitepiece3 = new Piece(1, 4, true, "runner", piecesList);
        Piece whitepiece4 = new Piece(0, 4, true, "tower", piecesList);
        Piece whitepiece5 = new Piece(3, 3, true, "pawn", piecesList);

        // Creating the board
        JFrame frame = new JFrame();
        frame.setBounds(600, 250, 256, 350);
        frame.setUndecorated(true);
        JPanel pn = new JPanel() {
            @Override
            public void paint(Graphics gameBoard) {
                for (int j = 0; j < 5; j++) {
                    for (int i = 0; i < 4; i++) {
                        if ((i + j) % 2 == 0) {
                            gameBoard.setColor(new Color(244, 186, 135));
                        } else {
                            gameBoard.setColor(new Color(95, 51, 31));
                        }
                        gameBoard.fillRect(i * 64, j * 64, 64, 64);
                    }
                }
                // Naming each sub image
                for (Piece piece : piecesList) {
                    int imagePieceCount = 0;
                    switch (piece.name) {
                        case "king":
                            imagePieceCount = 0;
                            break;
                        case "queen":
                            imagePieceCount = 1;
                            break;
                        case "runner":
                            imagePieceCount = 2;
                            break;
                        case "jumper":
                            imagePieceCount = 3;
                            break;
                        case "tower":
                            imagePieceCount = 4;
                            break;
                        case "pawn":
                            imagePieceCount = 5;
                            break;
                    }
                    if (!piece.isBlack) {
                        imagePieceCount += 6;
                    }
                    gameBoard.drawImage(imgs[imagePieceCount], piece.i, piece.j, this);
                }
                // Highlighting valid moves - TRANSPARENT GREEN
                if (selectedPiece != null) {
                    for (Point move : getValidMoves(selectedPiece)) {
                        int moveX = move.x * 64;
                        int moveY = move.y * 64;
                        gameBoard.setColor(new Color(0, 255, 0, 128));
                        gameBoard.fillRect(moveX, moveY, 64, 64);
                    }
                }

                // Draw quit button
                gameBoard.setColor(Color.WHITE);
                gameBoard.fillRect(0, 320, 256, 32);
                gameBoard.setColor(Color.BLACK);
                gameBoard.drawString("FORFEIT", 100, 340);
            }
        };
        frame.add(pn);
        frame.addMouseListener(new MouseListener() {

            // Get the piece that the mouse presses on
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println((getPiece(e.getX(), e.getY()).isBlack ? "White " : "Black ")
                        + getPiece(e.getX(), e.getY()).name);
                if (isBlackTurn && getPiece(e.getX(), e.getY()).isBlack
                        || !isBlackTurn && !getPiece(e.getX(), e.getY()).isBlack) {
                    selectedPiece = getPiece(e.getX(), e.getY());
                } else {
                    JOptionPane.showMessageDialog(frame, "INVALID MOVE", "ERROR", JOptionPane.ERROR_MESSAGE);
                    selectedPiece = null;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedPiece != null) {
                    int xPos = e.getX() / 64;
                    int yPos = e.getY() / 64;

                    // Rules for Pawn Promotion
                    if (PieceMovement(selectedPiece, xPos, yPos)) {
                        if (selectedPiece.name == "pawn" && (yPos == 0 || yPos == 4)) {
                            // Prompt the player to choose a piece for promotion
                            String[] promotionOptions = { "TOWER", "RUNNER", "JUMPER", "QUEEN" };
                            int choice = JOptionPane.showOptionDialog(
                                    messageFrame,
                                    "CHOOSE PIECE TO PROMOTE PAWN:",
                                    "Pawn Promotion",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    promotionOptions,
                                    promotionOptions[0]);

                            String promotion;
                            switch (choice) {
                                case 0:
                                    promotion = "tower";
                                    break;
                                case 1:
                                    promotion = "runner";
                                    break;
                                case 2:
                                    promotion = "jumper";
                                    break;
                                case 3:
                                    promotion = "queen";
                                    break;
                                default:
                                    promotion = "tower";
                                    break;
                            }
                            selectedPiece.promotePawn(promotion);
                        } else {
                            selectedPiece.move(xPos, yPos);
                        }
                        isBlackTurn = !isBlackTurn; // Switch turns

                        // If Checkmate , end game
                        if (isCheckmate(isBlackTurn)) {
                            String winner = isBlackTurn ? "WHITE" : "BLACK";
                            JOptionPane.showMessageDialog(
                                    messageFrame,
                                    "CHECKMATE! " + winner + " WINS!",
                                    "GAME OVER",
                                    JOptionPane.INFORMATION_MESSAGE);
                            System.exit(0);
                        }
                    } else {
                        selectedPiece.resetPosition();
                        JOptionPane.showMessageDialog(
                                messageFrame, "INVALID MOVE", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                    selectedPiece = null;
                    frame.repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                int x = e.getX() / 64;
                int y = e.getY() / 64;

                // Check if the quit button was clicked
                if (y == 5) {
                    if (x >= 0 && x <= 3) {
                        String winner = isBlackTurn ? "BLACK" : "WHITE";
                        JOptionPane.showMessageDialog(
                                messageFrame,
                                "TEAM " + winner + " WINS!",
                                "GAME OVER",
                                JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });
        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPiece != null) {
                    selectedPiece.i = e.getX() - 32;
                    selectedPiece.j = e.getY() - 32;
                    frame.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }

    public static Piece getPiece(int i, int j) {
        int xPos = i / 64;
        int yPos = j / 64;
        for (Piece piece : piecesList) {
            if (piece.xPos == xPos && piece.yPos == yPos) {
                return piece;
            }
        }
        return null;
    }

    // Valid Movements for the pieces
    public static boolean PieceMovement(Piece piece, int xPos, int yPos) {
        if (piece.name == "king") {
            int absX = Math.abs(xPos - piece.xPos);
            int absY = Math.abs(yPos - piece.yPos);
            int destX = xPos * 64;
            int destY = yPos * 64;
            return ((absX == 1 && absY == 1) || (absX == 0 && absY == 1) || (absX == 1 && absY == 0)) && (getPiece(destX, destY) == null || getPiece(destX, destY).isBlack != piece.isBlack);

        } else if (piece.name == "jumper") {
            int absX = Math.abs(xPos - piece.xPos);
            int absY = Math.abs(yPos - piece.yPos); 
            int destX = xPos * 64;
            int destY = yPos * 64;
            return absX * absY == 2 && (getPiece(destX, destY) == null || getPiece(destX, destY).isBlack != piece.isBlack);
           
        }else if (piece.name.equals("runner")) {
            int absX = xPos - piece.xPos;
            int absY = yPos - piece.yPos;
        
            // Check for diagonal
            if (Math.abs(absX) == Math.abs(absY)) {
                int xMove = Integer.compare(absX, 0); // X direction (+1 or -1)
                int yMove = Integer.compare(absY, 0); // Y direction (+1 or -1)
        
                // Check for obstacles along the diagonal path
                int x = piece.xPos + xMove;
                int y = piece.yPos + yMove;
        
                while (x != xPos && y != yPos) {
                    if (getPiece(x * 64, y * 64) != null) {
                        return false; // Obstacle found, invalid move
                    }
                    x += xMove;
                    y += yMove;
                }
        
                // Destination square is either empty or has an opposing piece
                int destX = xPos * 64;
                int destY = yPos * 64;
                return getPiece(destX, destY) == null || getPiece(destX, destY).isBlack != piece.isBlack;
            }
        
    
        } else if (piece.name == "pawn") {
            int absX = xPos - piece.xPos;
            int absY = yPos - piece.yPos;
            if (piece.isBlack) {
                // Normal forward move
                if (absX == 0 && absY == -1 && ChessGame.getPiece(xPos * 64, yPos * 64) == null) {
                    return true;
                }
                // Capture diagonally
                if (Math.abs(absX) == 1 && absY == -1 && ChessGame.getPiece(xPos * 64, yPos * 64) != null
                        && ChessGame.getPiece(xPos * 64, yPos * 64).isBlack != piece.isBlack) {
                    return true;
                }
            } else {
                // Normal forward move
                if (absX == 0 && absY == 1 && ChessGame.getPiece(xPos * 64, yPos * 64) == null) {
                    return true;
                }
                // Capture diagonally
                if (Math.abs(absX) == 1 && absY == 1 && ChessGame.getPiece(xPos * 64, yPos * 64) != null
                        && ChessGame.getPiece(xPos * 64, yPos * 64).isBlack != piece.isBlack) {
                    return true;
                }
            }

        } else if (piece.name == "tower") {
            int absX = xPos - piece.xPos;
            int absY = yPos - piece.yPos;

            // Check if the move is horizontal or vertical
            if (absX == 0 || absY == 0) {
                int steps = Math.max(Math.abs(absX), Math.abs(absY));
                int xMove = Integer.compare(absX, 0); // X direction (+1, 0, or -1)
                int yMove = Integer.compare(absY, 0); // Y direction (+1, 0, or -1)

                // Check for obstacles along the horizontal or vertical path
                int x = piece.xPos + xMove;
                int y = piece.yPos + yMove;
                for (int i = 1; i < steps; i++) {
                    if (getPiece(x * 64, y * 64) != null) {
                        return false; // Obstacle found, invalid move
                    }
                    x += xMove;
                    y += yMove;
                }

                // Destination square is either empty or has an opposing piece
                return getPiece(xPos * 64, yPos * 64) == null
                        || getPiece(xPos * 64, yPos * 64).isBlack != piece.isBlack;
            }
        } else if (piece.name == "queen") {
            int absX = xPos - piece.xPos;
            int absY = yPos - piece.yPos;

            // Check if the move is horizontal, vertical, or diagonal
            if (absX == 0 || absY == 0 || Math.abs(absX) == Math.abs(absY)) {
                int steps = Math.max(Math.abs(absX), Math.abs(absY));
                int xMove = Integer.compare(absX, 0); // X direction (+1, 0, or -1)
                int yMove = Integer.compare(absY, 0); // Y direction (+1, 0, or -1)

                // Check for obstacles along the path
                int x = piece.xPos + xMove;
                int y = piece.yPos + yMove;
                for (int i = 1; i < steps; i++) {
                    if (getPiece(x * 64, y * 64) != null) {
                        return false; // Obstacle found, invalid move
                    }
                    x += xMove;
                    y += yMove;
                }

                // Destination square is either empty or has an opposing piece
                return getPiece(xPos * 64, yPos * 64) == null
                        || getPiece(xPos * 64, yPos * 64).isBlack != piece.isBlack;
            }
        }
        return false;
    }

    public static LinkedList<Point> getValidMoves(Piece piece) {
        LinkedList<Point> validMoves = new LinkedList<>();
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 4; i++) {
                if (PieceMovement(piece, i, j)) {
                    validMoves.add(new Point(i, j));
                }
            }
        }
        return validMoves;
    }
//Check
    public static void IsInCheck() {
        isCheck = false;
        Piece king = null;
    
        // Find the current player's king
        for (Piece piece : piecesList) {
            if (piece.isBlack == isBlackTurn && piece.name=="king") {
                king = piece;
                break;
            }
        }
    
        // Check if any opposing piece threatens the king
        if (king != null) {
            for (Piece piece : piecesList) {
                if (piece.isBlack != isBlackTurn && PieceMovement(piece, king.xPos, king.yPos)) {
                    isCheck = true;
                    break;
                }
            }
        }
    }
//Checkmate   
    public static boolean isCheckmate(boolean isBlackTurn) {
        if (!isCheck) {
            return false;
        }
    
        for (Piece piece : piecesList) {
            if (piece.isBlack == isBlackTurn) {
                LinkedList<Point> validMoves = getValidMoves(piece);
    
                // Check if it can escape check
                for (Point move : validMoves) {
                    Piece capturedPiece = getPiece(move.x, move.y);
                    int prevX = piece.xPos;
                    int prevY = piece.yPos;
    
                    // Move the piece temporarily and check for check again
                    piece.move(move.x, move.y);
                    IsInCheck();
    
                    if (!isCheck) {
                        // The check is escaped, so it's not checkmate
                        piece.move(prevX, prevY); // Restore the previous position
                        if (capturedPiece != null) {
                            piecesList.add(capturedPiece); // Restore the captured piece
                        }
                        return false;
                    }
    
                    piece.move(prevX, prevY); 
                    if (capturedPiece != null) {
                        piecesList.add(capturedPiece); 
                    }
                }
            }
        }
    
        // Checkmate
        return true;
    }
    
}

