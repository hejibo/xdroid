package dev.orbit.apps.movingmap.net;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class DatagramDecoder {

	public static Map<Integer, float[]> DecodePacket(DatagramPacket packet) {
		int sentenceCount = (packet.getLength() - 5) / (9 * 4);
		int sentenceLengthBytes = 32;
		int sentenceIdLengthBytes = 4;
		byte[] buffer = packet.getData();

		// remove headers and convert to floating point array
		Map<Integer, float[]> sentences = new HashMap<Integer, float[]>();

		for (int sentenceOffset = 5; sentenceOffset < packet.getLength(); sentenceOffset += sentenceLengthBytes
				+ sentenceIdLengthBytes) {
			int sentenceId = buffer[sentenceOffset];
			float[] data = new float[8];
			int dataPointIndex = 0;

			// iterate through the sentence, offset by the
			// length of the header and convert bytes to floats
			for (int z = sentenceOffset + sentenceIdLengthBytes; z < sentenceOffset
					+ sentenceIdLengthBytes + sentenceLengthBytes; z += 4) {
				byte b1 = buffer[z];
				byte b2 = buffer[z + 1];
				byte b3 = buffer[z + 2];
				byte b4 = buffer[z + 3];

				int floatBits = ((b4 & 0xff) << 24) + ((b3 & 0xff) << 16)
						+ ((b2 & 0xff) << 8) + (b1 & 0xff);

				
				data[dataPointIndex] = Float.intBitsToFloat(floatBits);
				dataPointIndex++;
			}
			
			sentences.put(sentenceId, data);
		}

		String inputLine = String.format("Sentence Count: %1$d", sentenceCount);
		Log.d(DatagramDecoder.class.getSimpleName(), inputLine);
		
		// log some points for validation
		for (Integer sentenceId : sentences.keySet()) {
			final StringBuffer sb = new StringBuffer(String.format(
					"SentenceId: %1$d::", sentenceId));

			float[] data = sentences.get(sentenceId);
			for (float item : data) {
				sb.append(String.format("%1$4.3f,", item));
			}
			Log.d(DatagramDecoder.class.getSimpleName(), sb.toString());
		}

		// reset the length before reusing
		packet.setLength(buffer.length);
		return sentences;
	}
}
