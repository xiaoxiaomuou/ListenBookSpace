/**
 *  Author :  hmg25
 *  Description :
 */
package com.hwang.listenbook.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

public class BookPageFactory {

	private File book_file = null;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	private String m_strCharsetName;// TXT��ʽ
	public Bitmap m_book_bg = null;
	private int mWidth;
	private int mHeight;
	public static String strPercent = "0.0";
	public static Float fPercent = 0.0f;

	private Vector<String> m_lines = new Vector<String>();

	private int m_fontSize = 56;
	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffffffff; // ������ɫ
	private int marginWidth = 30; // �������Ե�ľ���
	private int marginHeight = 40; // �������Ե�ľ���
	private int mLineMarginHeight = 0;

	private int mLineCount; // ÿҳ������ʾ������
	private float mVisibleHeight; // �������ݵĿ�
	private float mVisibleWidth; // �������ݵĿ�
	private boolean m_isfirstPage, m_islastPage;

	// private int m_nLineSpaceing = 5;

	private Paint mPaint;

	public String convertCodeAndGetText(String strFilePath) {// �жϵ�ǰ��ʽ
		File file = new File(strFilePath);

		try {
			// FileReader f_reader = new FileReader(file);
			// BufferedReader reader = new BufferedReader(f_reader);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fis);
			in.mark(4);
			byte[] first3bytes = new byte[3];
			in.read(first3bytes);// �ҵ��ĵ���ǰ����ֽڲ��Զ��ж��ĵ����͡�
			in.reset();
			if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
					&& first3bytes[2] == (byte) 0xBF) {// utf-8

				m_strCharsetName = "UTF-8";

			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFE) {

				m_strCharsetName = "Unicode";
			} else if (first3bytes[0] == (byte) 0xFE
					&& first3bytes[1] == (byte) 0xFF) {

				m_strCharsetName = "UTF-16BE";
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFF) {

				m_strCharsetName = "UTF-16LE";
			} else {

				m_strCharsetName = "GBK";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return m_strCharsetName;
	}

	public BookPageFactory(int w, int h) {
		// TODO Auto-generated constructor stub
		mWidth = w;
		mHeight = h;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(m_fontSize);
		mPaint.setColor(m_textColor);
		mPaint.setSubpixelText(true);
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight * 2;
		mLineCount = (int) (mVisibleHeight / (m_fontSize+mLineMarginHeight)); // ����ʾ������
	}

	
	public void openbook(String strFilePath) throws IOException {

		book_file = new File(strFilePath);
		convertCodeAndGetText(strFilePath);
		long lLen = book_file.length();
		m_mbBufLen = (int) lLen;
		m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);
		
		

//        System.out.println("----~~~~----"+book_file);
	}

	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		}
		 else if (m_strCharsetName.equals("Unicode")) {
			 i = nEnd - 2;
				while (i > 0) {
					b0 = m_mbBuf.get(i);
					b1 = m_mbBuf.get(i + 1);
					if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
						i += 2;
						break;
					}
					i--;
				}
				}
		
		else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		j=0;
		byte[] buf = new byte[nParaSize];
		if (m_strCharsetName.equals("Unicode")) {
			buf[0]=-1;
		    buf[1]=-2;
		    j=2;
		}
		
		for (; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}

	// ��ȡ��һ����
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// ��ݱ����ʽ�жϻ���
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		}
		
		
		
		
		 else if (m_strCharsetName.equals("Unicode")) {
			 while (i < m_mbBufLen - 1) {
					b0 = m_mbBuf.get(i++);
					b1 = m_mbBuf.get(i++);
					if (b0 == 0x0a && b1 == 0x00) {
						break;
					}
				}
		 }
		 

		else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
         
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		i=0;
		if (m_strCharsetName.equals("Unicode")) {
			buf[0]=-1;
			buf[1]=-2;
			i=2;
		}
		for (; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	public Vector<String> pageDown() { // ���·�

		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // ��ȡһ������
			m_mbBufEnd += paraBuf.length;
			try {
				
				strParagraph = new String(paraBuf, m_strCharsetName);}
			 catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	protected void pageUp() {

		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			 Log.d("hwang","A="+m_mbBufBegin+"B="+paraBuf+"C="+paraBuf.length);
			try {
				
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	public void gotoPage(float fPercent) {
		m_lines.clear();
		m_mbBufBegin = (int) (m_mbBufLen * fPercent / 100f);
		pageUp();
		m_lines = pageDown();
		if (m_mbBufBegin == 0) {
               
		} else {
			m_mbBufBegin = m_mbBufEnd;
			m_lines = pageDown();
		}

	}

	public void prePage() throws IOException {
		Log.d("hwang", "the m_mbBufBegin is ==>"+m_mbBufBegin);
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			m_isfirstPage = true;
			return;
		} else
			m_isfirstPage = false;
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
		//m_islastPage = false;
	}

	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage = true;
			return;
		} else
			m_islastPage = false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
		//m_isfirstPage = false;
	}

	public void onDraw(Canvas c) {
		Log.d("hwang", "this is onDraw=====");
		if (m_lines.size() == 0)
			m_lines = pageDown();
		if (m_lines.size() > 0) {
			if (m_book_bg == null)
				c.drawColor(m_backColor);

			else
				c.drawBitmap(m_book_bg, 0, 0, null);
			int y = marginHeight;
			for (String strLine : m_lines) {
				y += m_fontSize;
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}

		fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);// ��ǰ�ֽ�/���ֽ�
		DecimalFormat df = new DecimalFormat("#0.0");
		strPercent = df.format(fPercent * 100) + "%";
		strPercent=strPercent.replace(',', '.');
		int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);// �ػ�ٷֱ��±�

	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}

	public boolean isfirstPage() {
		return m_isfirstPage;
	}

	public boolean islastPage() {
		return m_islastPage;
	}
}
