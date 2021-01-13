package projeto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DownloadWindow {
	private JFrame frame;
	private TheISCTEBay theISCTEBay;
	private DefaultListModel<String> files = new DefaultListModel<String>();
	private JList list;

	public DownloadWindow(TheISCTEBay c) {
		this.theISCTEBay = c;
		frame = new JFrame(c.getNome());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				theISCTEBay.transmitData("DC");
			}
		});
		frame.setLayout(new BorderLayout());
		addFrameContent();
		frame.setResizable(false);
		frame.pack();
	}

	public void open() {
		frame.setVisible(true);
	}

	public void addFrameContent() {
		JPanel searchBar = new JPanel();
		searchBar.setLayout(new FlowLayout());
		JLabel searchLabel = new JLabel("Texto a procurar:");
		JTextField searchText = new JTextField();
		searchText.setPreferredSize(new Dimension(100,20));
		JButton searchButton = new JButton("Procurar");
		searchBar.add(searchLabel);
		searchBar.add(searchText);
		searchBar.add(searchButton);
		searchButton.addActionListener(new ActionListener() { 
			@Override
			public synchronized void actionPerformed(ActionEvent arg0) {
				files.removeAllElements();
				System.out.println(searchText.getText());
				theISCTEBay.setKeyword(searchText.getText());
				theISCTEBay.transmitData("SCH");
				try {
					TheISCTEBay.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ArrayList<String> newFiles = theISCTEBay.getFilesToDisplay();
				for(String s : newFiles) {
					files.addElement(s.split(" ")[0] +" "+ s.split(" ")[1] + " bytes");    
				}
			}
		});
		frame.add(searchBar, BorderLayout.NORTH);
		JList<String> resultlist = new JList<String>(files);
		resultlist.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				list = (JList)e.getSource();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		});
		frame.add(new JScrollPane(resultlist),BorderLayout.WEST);
		resultlist.addListSelectionListener(new ListSelectionListener() {
			private int previous = -1;

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (resultlist.getSelectedIndex() != -1
						&& previous != resultlist.getSelectedIndex()) {
				}
				previous = resultlist.getSelectedIndex();
			}
		});
		JPanel download = new JPanel();
		download.setLayout(new BorderLayout());
		JButton downloadButton = new JButton("Descarregar");
		downloadButton.addActionListener(new ActionListener() { 
			@Override
			public synchronized void actionPerformed(ActionEvent arg0) {
				theISCTEBay.downloadFile((String)list.getSelectedValue());
			}
		});
		downloadButton.setPreferredSize(new Dimension(150,70));
		JProgressBar bar = new JProgressBar();
		bar.setPreferredSize(new Dimension(150,70));
		download.add(downloadButton, BorderLayout.NORTH);
		download.add(bar, BorderLayout.SOUTH);
		frame.add(download, BorderLayout.EAST);
	}
}
