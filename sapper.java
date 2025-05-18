import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class virtualCell{
    boolean isMin = false;
    int nubmerOfMins = 0;
    boolean flag = false;
    boolean revealed = false;
}

class virtualField{
    int height;
    int width;
    int mines;
    virtualCell [][] virtualField;
    
    public virtualField(int height, int width, int mines){
        this.height = height;
        this.width = width;
        this.mines = mines;
        this.virtualField = new virtualCell[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                virtualField[row][col] = new virtualCell(); 
            }
        }

        for(int i=0;i<mines;i++){
            int randomNum = (int) (Math.random()*height*width);
            int rowOfMin = randomNum/width;
            int colOfMin = randomNum%width;

            if (virtualField[rowOfMin][colOfMin].isMin == false){
                virtualField[rowOfMin][colOfMin].isMin = true;
                
                int iRow, fRow, iCol, fCol;
                iRow = (rowOfMin==0)?0:(rowOfMin-1);
                fRow = (rowOfMin==height-1)?rowOfMin:(rowOfMin+1);
                iCol = (colOfMin==0)?0:(colOfMin-1);
                fCol = (colOfMin==width-1)?colOfMin:(colOfMin+1);
                for (int row = iRow; row < fRow+1; row++) {
                    for (int col = iCol; col < fCol+1; col++) {
                        virtualField[row][col].nubmerOfMins+=1;
                    }
                }
            }
        }
    }
}

public class sapper {
    JFrame frame;
    JPanel mainPanel, gamePanel, controlPanel;
    JButton[][] cells;
    JButton restartButton;
    int height = 10;
    int width = 10;
    int mineCount = 10; 
    boolean gameOver = false;

    virtualField vf = new virtualField(height, width, mineCount);

    public static void main(String[] args) {
        sapper game = new sapper();
        game.initializeGame();
    }

    void initializeGame() {
        vf = new virtualField(height, width, mineCount);
        frame = new JFrame("Сапёр");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(144, 238, 144)); 
        
        createGamePanel();
        createControlPanel();
        
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void createGamePanel() {
        gamePanel = new JPanel(new GridLayout(height, width));
        cells = new JButton[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cells[i][j] = new JButton();
                cells[i][j].setPreferredSize(new Dimension(40, 40));
                styleCell(cells[i][j], false);
                
                int row = i;
                int col = j;
                
                cells[i][j].addMouseListener(new MouseAdapter() {
                    
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver) return;
                        
                        if (SwingUtilities.isRightMouseButton(e)) {
                            toggleFlag(row, col);
                        } else if (!vf.virtualField[row][col].flag) {
                            revealCell(row, col);
                        }
                    }
                });
                
                gamePanel.add(cells[i][j]);
            }
        }
        
        mainPanel.add(gamePanel, BorderLayout.CENTER);
    }
    
    void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(new Color(144, 238, 144)); 
        restartButton = new JButton("Новая игра");
        styleRestartButton(restartButton);
        restartButton.addActionListener(e -> restartGame());
        controlPanel.add(restartButton);
        mainPanel.add(controlPanel, BorderLayout.EAST);
    }

    void styleCell(JButton cell, boolean revealed) {
        if (revealed) {
            cell.setBackground(new Color(245, 245, 220)); 
        } else {
            cell.setBackground(new Color(210, 180, 140)); 
        }
        cell.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2));
        cell.setFont(new Font("Arial", Font.BOLD, 16));
        cell.setFocusPainted(false);
    }
    
    void styleRestartButton(JButton button) {
        button.setBackground(new Color(245, 245, 220));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2));
        button.setFocusPainted(false);
    }

    void setNumberColor(JButton cell, int number) {
        switch (number) {
            case 1: cell.setForeground(Color.BLUE); break;
            case 2: cell.setForeground(Color.GREEN); break;
            case 3: cell.setForeground(Color.RED); break;
            case 4: cell.setForeground(Color.ORANGE); break;
            case 5: cell.setForeground(Color.MAGENTA); break;
            default: cell.setForeground(Color.BLACK);
        }
    }

    void toggleFlag(int row, int col) {
        if (vf.virtualField[row][col].revealed) return;
        vf.virtualField[row][col].flag = !vf.virtualField[row][col].flag;
        if (vf.virtualField[row][col].flag) {
            cells[row][col].setText("F");
        } else {
            cells[row][col].setText("");
        }
    }

    void revealCell(int row, int col) {
        if (vf.virtualField[row][col].revealed || vf.virtualField[row][col].flag) return;
        vf.virtualField[row][col].revealed = true;
        styleCell(cells[row][col], true);
        
        if (vf.virtualField[row][col].isMin) {
            gameOver();
            cells[row][col].setText("*");
            cells[row][col].setBackground(Color.RED);
            return;
        }
        
        
        if (vf.virtualField[row][col].nubmerOfMins > 0) {
            cells[row][col].setText(String.valueOf(vf.virtualField[row][col].nubmerOfMins));
            setNumberColor(cells[row][col], vf.virtualField[row][col].nubmerOfMins);
        } else {
            int iRow, fRow, iCol, fCol;
            iRow = (row==0)?0:(row-1);
            fRow = (row==height-1)?row:(row+1);
            iCol = (col==0)?0:(col-1);
            fCol = (col==width-1)?col:(col+1);
            for (int i = iRow; i < fRow+1; i++) {
                for (int j = iCol; j < fCol+1; j++) {
                    revealCell(i, j);
                }
            }   
        }
        
        checkWin();
    }
    
    void gameOver() {
        gameOver = true;
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (vf.virtualField[i][j].isMin && !vf.virtualField[i][j].flag) {
                    cells[i][j].setText("*");
                    cells[i][j].setBackground(Color.RED);
                }
            }
        }
        
        JOptionPane.showMessageDialog(frame, "Вы проиграли! Попробуйте еще раз.", "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
    }

    void checkWin() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!vf.virtualField[i][j].isMin && !vf.virtualField[i][j].revealed) {
                    return; 
                }
            }
        }
        
        gameOver = true;
        JOptionPane.showMessageDialog(frame, "Поздравляем! Вы выиграли!", "Победа!", JOptionPane.INFORMATION_MESSAGE);
    }

    void restartGame() {
        frame.dispose();
        gameOver = false;
        initializeGame();
    }

}
