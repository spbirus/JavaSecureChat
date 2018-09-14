import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    //port #5678 for improvedchat
    //port #8765 for securechat
    public static final int PORT = 8765;
    private BigInteger E, D, N;

    BufferedReader myReader;
    PrintWriter myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;
	ObjectOutputStream tempWriter;
	ObjectInputStream tempReader;
	private SymCipher cipher;

    public SecureChatClient ()
    {
        try {

        //prompt for the address and port
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr =
                InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new
                                               // Socket
        tempWriter =
				new ObjectOutputStream(connection.getOutputStream());
		tempWriter.flush();
		tempReader =
				new ObjectInputStream(connection.getInputStream());

        //reads in the values of E and N as well as the type of cipher
        E = (BigInteger) tempReader.readObject();
        System.out.println("E: " + E);
		N = (BigInteger) tempReader.readObject();
        System.out.println("N: " + N);
		String type = (String) tempReader.readObject();
		System.out.println("Chosen Cipher is: " + type);

		
		if(type.equals("Add")){
			cipher = new Add128();
		}else{
			cipher = new Substitute();
		}

		//get key from cipher object and convert to a BigInt
		BigInteger bigKey = new BigInteger(1, cipher.getKey());

		//RSA encrypt and send the cipher
        System.out.println("Decrypted key: " + bigKey);            
		BigInteger enKey = bigKey.modPow(E, N);
        System.out.println("Encrypted key: " + enKey);
		tempWriter.writeObject(enKey);


		//print the key as a byte array
		byte[] key = bigKey.toByteArray();
        System.out.println("Byte array length: " + key.length);
		System.out.println("Key: ");
		for (int i = 0; i < key.length; i++)
			System.out.print(key[i] + " ");
        System.out.println("\n");

		//ask for name and sent it to the server
		myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
		byte[] name = cipher.encode(myName);
        tempWriter.writeObject(name);   // Send name to Server.  Server will need
                                    // this to announce sign-on and sign-off
                                    // of clients
        tempWriter.flush();
        this.setTitle(myName);      // Set title to identify chatter

        //dont know if i still need this or not
        myReader =
            new BufferedReader(
                new InputStreamReader(
                    connection.getInputStream()));   // Get Reader and Writer

        myWriter =
             new PrintWriter(
                 new BufferedWriter(
                     new OutputStreamWriter(connection.getOutputStream())), true);


        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

		addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    { 
                        String close = "CLIENT CLOSING";
                        byte[] closeMsg = cipher.encode(close);
                        System.out.println(close);
                        try {
                            tempWriter.writeObject(closeMsg);
                        } catch (IOException ex) {
                            System.out.println("Error");
                        }
                        System.exit(0);
                    }
                }
            );

        setSize(500, 225);
        setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try {
                byte[] newBytes =(byte[]) tempReader.readObject();
                System.out.println("\n\nArray of bytes recieved: ");
                for (int i = 0; i < newBytes.length; i++) //print out the received array of bytes
                    System.out.print(newBytes[i] + " ");

                //NO WAY TO PRINT DECODED BYTES HERE, SO DO IT IN ADD128 AND SUBSTITUTE

                String currMsg = cipher.decode(newBytes);
                System.out.println("\nCorresponding String: \"" + currMsg +"\"");
			    outputArea.append(currMsg+"\n");
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        try {
            String currMsg = e.getActionCommand();    // Get input value
            byte[] newBytes = null;

            currMsg = myName + ": " + currMsg; //encode the sender name as well
            System.out.println("Original String Message: \"" + currMsg + "\""); //print out original message
            System.out.println("Corresponding array of bytes: ");
            byte[] originalBytes = currMsg.getBytes();
            for (int i = 0; i < originalBytes.length; i++) //print out the original array of bytes
                System.out.print(originalBytes[i] + " ");

            newBytes = cipher.encode(currMsg); //encode the message
            System.out.println("\nEncrypted array of Bytes: ");
            for (int i = 0; i < newBytes.length; i++) //print out the encoded array of bytes
                System.out.print(newBytes[i] + " ");

            inputField.setText("");
            
            tempWriter.writeObject(newBytes); //send the message as a byte array
        } catch (IOException ex) {
            System.out.println("actionPerformed exception: " + ex);
            //Logger.getLogger(SecureChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }                                               

    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}


