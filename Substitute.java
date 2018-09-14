import java.util.*;

public class Substitute implements SymCipher {
	private byte[] subKey = new byte[256];
	private byte[] decKey = new byte[256];

	public Substitute(){
		List<Byte> temp = new ArrayList<Byte>();
		for(int i = -128; i<128; i++){
			temp.add((byte)i);
		}
		//randomize the bytes
		Collections.shuffle(temp);
		Byte[] temp2 = new Byte[256];
		temp2 = temp.toArray(temp2);

		for(int i = 0; i < 256; i++){
			subKey[i] = temp2[i];
		}
	}

	public Substitute(byte[] ar){
		subKey = ar;
	}

	// Return an array of bytes that represent the key for the cipher
	public byte [] getKey(){
		return subKey;
	}	
	
	// Encode the string using the key and return the result as an array of
	// bytes.  Note that you will need to convert the String to an array of bytes
	// prior to encrypting it.  Also note that String S could have an arbitrary
	// length, so your cipher may have to "wrap" when encrypting.
	public byte [] encode(String S){
		byte[] b = S.getBytes();
		int i = 0;
		while(i < b.length){
			byte val = b[i];
			b[i] = subKey[val];
			i++;
		}
		return b;
	}
	
	// Decrypt the array of bytes and generate and return the corresponding String.
	public String decode(byte [] bytes){
		decKey = flipKey(subKey);
		for(int i = 0; i < bytes.length; i++){
			bytes[i] = (byte)decKey[bytes[i] + 128];
		}

		System.out.println("\nDecrypted array of Bytes: ");
            for (int j = 0; j < bytes.length; j++) //print out the decrypted array of bytes
                System.out.print(bytes[j] + " ");
                
		String s = new String(bytes);
		return s;
	}

	//flips the subKey array
	private byte[] flipKey(byte[] b){
		byte[] rev = new byte[256];
		for(int i = 0; i < 256; i++){
			//accounts for neg values
			rev[b[i] +128] = (byte)i;
		}
		return rev;
	}

	//test program
	public static void main(String[] args){
		String s = "abcdefghijklmnopqrstuvwxyz";

		System.out.println("Default constructor");
		Add128 add = new Add128();
		System.out.println("Key: " + add.getKey().toString());
		byte[] en = add.encode(s);
		System.out.println("Encode: " + en.toString());
		String dec = add.decode(en);
		System.out.println("Decode: " + dec);
	}
}