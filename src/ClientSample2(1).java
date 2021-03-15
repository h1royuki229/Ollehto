//�p�b�P�[�W�̃C���|�[�g
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class ClientSample2 extends JFrame implements MouseListener {
	private JButton buttonArray[];//�I�Z���՗p�̃{�^���z��
	private JButton stop, pass; //��~�A�X�L�b�v�p�{�^��
	private JLabel colorLabel; // �F�\���p���x��
	private JLabel turnLabel; // ��ԕ\���p���x��
	private Container c; // �R���e�i
	private ImageIcon blackIcon, whiteIcon, boardIcon; //�A�C�R��
	private PrintWriter out;//�f�[�^���M�p�I�u�W�F�N�g
	private Receiver receiver; //�f�[�^��M�p�I�u�W�F�N�g

	// �R���X�g���N�^
	public ClientSample2() {
		//�e�X�g�p�ɋǖʏ���������
		String [] grids = 
			{"board","board","board","board","board","board","board","board",
			"board","board","board","board","board","board","board","board",
			"board","board","board","board","board","board","board","board",
			"board","board","board","black","white","board","board","board",
			"board","board","board","white","black","board","board","board",
			"board","board","board","board","board","board","board","board",
			"board","board","board","board","board","board","board","board",
			"board","board","board","board","board","board","board","board"};
		int row = 8; //�I�Z���Ղ̏c���}�X�̐�
		//�E�B���h�E�ݒ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����ꍇ�̏���
		setTitle("�l�b�g���[�N�ΐ�^�I�Z���Q�[��");//�E�B���h�E�̃^�C�g��
		setSize(row * 45 + 10, row * 45 + 200);//�E�B���h�E�̃T�C�Y��ݒ�
		c = getContentPane();//�t���[���̃y�C�����擾
		//�A�C�R���ݒ�(�摜�t�@�C�����A�C�R���Ƃ��Ďg��)
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
		c.setLayout(null);//
		//�I�Z���Ղ̐���
		buttonArray = new JButton[row * row];//�{�^���̔z����쐬
		for(int i = 0 ; i < row * row ; i++){
			if(grids[i].equals("black")){ buttonArray[i] = new JButton(blackIcon);}//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
			if(grids[i].equals("white")){ buttonArray[i] = new JButton(whiteIcon);}//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
			if(grids[i].equals("board")){ buttonArray[i] = new JButton(boardIcon);}//�Ֆʏ�Ԃɉ������A�C�R����ݒ�
			c.add(buttonArray[i]);//�{�^���̔z����y�C���ɓ\��t��
			// �{�^����z�u����
			int x = (i % row) * 45;
			int y = (int) (i / row) * 45;
			buttonArray[i].setBounds(x, y, 45, 45);//�{�^���̑傫���ƈʒu��ݒ肷��D
			buttonArray[i].addMouseListener(this);//�}�E�X�����F���ł���悤�ɂ���
			buttonArray[i].setActionCommand(Integer.toString(i));//�{�^�������ʂ��邽�߂̖��O(�ԍ�)��t������
		}
		//�I���{�^��
		stop = new JButton("�I��");//�I���{�^�����쐬
		c.add(stop); //�I���{�^�����y�C���ɓ\��t��
		stop.setBounds(0, row * 45 + 30, (row * 45 + 10) / 2, 30);//�I���{�^���̋��E��ݒ�
		stop.addMouseListener(this);//�}�E�X�����F���ł���悤�ɂ���
		stop.setActionCommand("stop");//�{�^�������ʂ��邽�߂̖��O��t������
		//�p�X�{�^��
		pass = new JButton("�p�X");//�p�X�{�^�����쐬
		c.add(pass); //�p�X�{�^�����y�C���ɓ\��t��
		pass.setBounds((row * 45 + 10) / 2, row * 45 + 30, (row * 45 + 10 ) / 2, 30);//�p�X�{�^���̋��E��ݒ�
		pass.addMouseListener(this);//�}�E�X�����F���ł���悤�ɂ���
		pass.setActionCommand("pass");//�{�^�������ʂ��邽�߂̖��O��t������
		//�F�\���p���x��
		colorLabel = new JLabel("�F�͖���ł�");//�F����\�����邽�߂̃��x�����쐬
		colorLabel.setBounds(10, row * 45 + 60 , row * 45 + 10, 30);//���E��ݒ�
		c.add(colorLabel);//�F�\���p���x�����y�C���ɓ\��t��
		//��ԕ\���p���x��
		turnLabel = new JLabel("��Ԃ͖���ł�");//��ԏ���\�����邽�߂̃��x�����쐬
		turnLabel.setBounds(10, row * 45 + 120, row * 45 + 10, 30);//���E��ݒ�
		c.add(turnLabel);//��ԏ�񃉃x�����y�C���ɓ\��t��
	}

	// ���\�b�h
	public void connectServer(String ipAddress, int port){	// �T�[�o�ɐڑ�
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); //�T�[�o(ipAddress, port)�ɐڑ�
			out = new PrintWriter(socket.getOutputStream(), true); //�f�[�^���M�p�I�u�W�F�N�g�̗p��
			receiver = new Receiver(socket); //��M�p�I�u�W�F�N�g�̏���
			receiver.start();//��M�p�I�u�W�F�N�g(�X���b�h)�N��
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g��IP�A�h���X������ł��܂���: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("�T�[�o�ڑ����ɃG���[���������܂���: " + e);
			System.exit(-1);
		}
	}

	public void sendMessage(String msg){	// �T�[�o�ɑ�����𑗐M
		out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
		out.flush();//���M�f�[�^�𑗂�
		System.out.println("�T�[�o�Ƀ��b�Z�[�W " + msg + " �𑗐M���܂���"); //�e�X�g�W���o��
	}

	// �f�[�^��M�p�X���b�h(�����N���X)
	class Receiver extends Thread {
		private InputStreamReader sisr; //��M�f�[�^�p�����X�g���[��
		private BufferedReader br; //�����X�g���[���p�̃o�b�t�@

		// �����N���XReceiver�̃R���X�g���N�^
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //��M�����o�C�g�f�[�^�𕶎��X�g���[����
				br = new BufferedReader(sisr);//�����X�g���[�����o�b�t�@�����O����
			} catch (IOException e) {
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}
		}
		// �����N���X Receiver�̃��\�b�h
		public void run(){
			try{
				while(true) {//�f�[�^����M��������
					String inputLine = br.readLine();//��M�f�[�^����s���ǂݍ���
					if (inputLine != null){//�f�[�^����M������
						receiveMessage(inputLine);//�f�[�^��M�p���\�b�h���Ăяo��
					}
				}
			} catch (IOException e){
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}
		}
	}

	public void receiveMessage(String msg){	// ���b�Z�[�W�̎�M
		System.out.println("�T�[�o���烁�b�Z�[�W " + msg + " ����M���܂���"); //�e�X�g�p�W���o��
	}
	public void updateDisp(){	// ��ʂ��X�V����
	}
	public void acceptOperation(String command){	// �v���C���̑������t
	}

  	//�}�E�X�N���b�N���̏���
	public void mouseClicked(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�L���X�g��Y�ꂸ��
		String command = theButton.getActionCommand();//�{�^���̖��O�����o��
		System.out.println("�}�E�X���N���b�N����܂����B�����ꂽ�{�^���� " + command + "�ł��B");//�e�X�g�p�ɕW���o��
		sendMessage(command); //�e�X�g�p�Ƀ��b�Z�[�W�𑗐M
	}
	public void mouseEntered(MouseEvent e) {}//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
	public void mouseExited(MouseEvent e) {}//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
	public void mousePressed(MouseEvent e) {}//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏���
	public void mouseReleased(MouseEvent e) {}//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���

	//�e�X�g�p��main
	public static void main(String args[]){ 
		ClientSample2 oclient = new ClientSample2();
		oclient.setVisible(true);
		oclient.connectServer("localhost", 10000);
	}
}