import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

public class editPhoto extends JFrame{
    private JPanel Wrapper;
    private JButton backButton;
    private JButton confirmButton;
    private JPanel photoPanel;
    private JScrollPane filtersScrollPane;
    private JButton filter1Button;
    private JButton filter2Button;
    private JButton filter3Button;
    private JLabel picLabel;
    private BufferedImage filtered;

    public editPhoto(java.util.List<BufferedImage> pics, List<String> names, Integer index, newPost info)
    {
        setContentPane(Wrapper);
        setTitle("editPhoto");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        BufferedImage photo = pics.get(index);
        picLabel.setIcon(new ImageIcon(photo));

        filter1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtered = new BufferedImage(photo.getWidth(),photo.getHeight(),BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < photo.getHeight(); y++) {
                    for (int x = 0; x < photo.getWidth(); x++) {
                        int rgba = photo.getRGB(x, y);
                        Color col = new Color(rgba, true);
                        int gray = (col.getRed() + col.getGreen() + col.getBlue()) / 3;
                        Color grayColor = new Color(gray, gray, gray, col.getAlpha());
                        filtered.setRGB(x, y, grayColor.getRGB());
                    }
                }
                picLabel.setIcon(new ImageIcon(filtered));
            }
        });

        filter2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtered = new BufferedImage(photo.getWidth(),photo.getHeight(),BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < photo.getHeight(); y++) {
                    for (int x = 0; x < photo.getWidth(); x++) {
                        int rgba = photo.getRGB(x, y);
                        Color col = new Color(rgba, true);
                        int brightness = 100;
                        int red = Math.min((col.getRed() + brightness), 255);
                        int green = Math.min((col.getGreen() + brightness), 255);
                        int blue= Math.min((col.getBlue() + brightness), 255);
                        Color saturatedColor = new Color(red, green, blue, col.getAlpha());
                        filtered.setRGB(x, y, saturatedColor.getRGB());
                    }
                }
                picLabel.setIcon(new ImageIcon(filtered));
            }
        });

        filter3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtered = new BufferedImage(photo.getWidth(),photo.getHeight(),BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < photo.getHeight(); y++)
                {
                    for (int x = 0; x < photo.getWidth(); x++)
                    {
                        int rgba = photo.getRGB(x, y);
                        Color col = new Color(rgba, true);
                        int brightness = 100;
                        int red = Math.min((col.getRed() + brightness), 255);
                        int green = col.getGreen();
                        int blue= Math.min((col.getBlue() + brightness), 255);
                        Color saturatedColor = new Color(red, green, blue, col.getAlpha());
                        filtered.setRGB(x, y, saturatedColor.getRGB());
                    }
                }
                picLabel.setIcon(new ImageIcon(filtered));
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                String tag = null;
                if     (info.eventRadio.isSelected())  tag = "event";
                else if(info.venueRadio.isSelected())  tag = "venue";
                else if(info.lessonRadio.isSelected()) tag = "lesson";
                else if(info.otherRadio.isSelected())  tag = "other";

                new newPost(info.tmp_user, tag, info.descTF.getText(), pics, names);
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                String tag = null;
                if     (info.eventRadio.isSelected())  tag = "event";
                else if(info.venueRadio.isSelected())  tag = "venue";
                else if(info.lessonRadio.isSelected()) tag = "lesson";
                else if(info.otherRadio.isSelected())  tag = "other";

                pics.set(index, filtered);
                new newPost(info.tmp_user, tag, info.descTF.getText(), pics, names);
            }
        });
    }
}
