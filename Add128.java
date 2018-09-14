import java.util.Random;

public class Add128 implements SymCipher{
	private byte[] addKey = new byte[128];

	public Add128(){
		Random rand = new Random();
		rand.nextBytes(addKey);		
	}

	public Add128(byte[] ar){
		addKey =  ar;
	}

	// Return an array of bytes that represent the key for the cipher
	public byte [] getKey(){
		return addKey;
	}	
	
	// Encode the string using the key and return the result as an array of
	// bytes.  Note that you will need to convert the String to an array of bytes
	// prior to encrypting it.  Also note that String S could have an arbitrary
	// length, so your cipher may have to "wrap" when encrypting.
	public byte [] encode(String S){
		byte[] b = S.getBytes();
		int i = 0;
		int k = 0;
		while(i < b.length){
			b[i] += addKey[k];
			i++;
			k++;
			//reset the counter for the key
			if(k >= 128){
				k=0;
			}
		}

		return b;
	}
	
	// Decrypt the array of bytes and generate and return the corresponding String.
	public String decode(byte [] bytes){
		
		int i = 0;
		int k = 0;
		while(i < bytes.length){
			bytes[i] -= addKey[k];
			i++;
			k++;
			//reset the counter for the key
			if(k >= 128){
				k=0;
			}
		}
		System.out.println("\nDecrypted array of Bytes: ");
            for (int j = 0; j < bytes.length; j++) //print out the decrypted array of bytes
                System.out.print(bytes[j] + " ");

		String s = new String(bytes); 
		return s;
	}

	//test program
	public static void main(String[] args){
		String s = "Hello World";

		System.out.println("Default constructor");
		Add128 add = new Add128();
		System.out.println("Key: " + add.getKey().toString());
		byte[] en = add.encode(s);
		System.out.println("Encode: " + en.toString());
		String dec = add.decode(en);
		System.out.println("Decode: " + dec);

		System.out.println("\nArray constructor");
		byte[] b = new byte[128];
		Random rand = new Random();
		rand.nextBytes(b);
		Add128 add2 = new Add128(b);
		System.out.println("Key: " + add2.getKey().toString());
		byte[] en2 = add2.encode(s);
		System.out.println("Encode: " + en2.toString());
		String dec2 = add2.decode(en2);
		System.out.println("Decode: " + dec2);
	}
}