package Digit;

import GD.Opts;
import Platform.CrossBitmap;
import Platform.Main;
import Supp.Colors;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ocr.OCRPlugin;

/**
 * Klasa realizujaca cala logike digitalizacji wykresu. 
 */
public class Digitalize
{
	
	public static List<Integer> detectColors(DigitInput in)
	{
		return detectColorsWithThr(in, 3, 10);
	}
		
	public static List<Integer> detectColorsWithThr(DigitInput in, double thr1, double thr2)
	{
		List<ColorsUtils.Color> colors;
		
		Integer[][][] image;

		if (in.options[Opts.oColorsDetectOn.id].is(Opts.oColorsDetectOn.LEGEND) &&
				in.legend != null)
			image = ImageUtils.bitmapToArray(in.legend);	
		else
			image = ImageUtils.bitmapToArray(in.graph);	
		
		List<Integer> set = ColorsUtils.getColorSet(image, true);
		
		List<Integer> cols = new ArrayList<>();
		for (Integer x : set)
		{
			if (CIELab.delta(in.background, x) > 10)
				cols.add(x);				
		}
		
		colors = ColorsUtils.detectColors(cols, thr1);
		
		cols.clear();
		for (ColorsUtils.Color c : colors)
		{
			cols.add(c.mean);
		}
		colors = ColorsUtils.detectColorsBetter(cols, thr2);
		
		List<Integer> result = new ArrayList<>();
		for (ColorsUtils.Color colRes : colors)
			result.add(colRes.mean);
		
		return result;
	}
		
	
	public static List<CrossBitmap> autoDetectElements(DigitInput in)
	{
		return null;
	}
			
	
	
	
	public static DigitOutput digitalize(DigitInput in)
	{
		return new DigitOutput();
	}
	
	
	public static List<Point>[] digiNormalColors(CrossBitmap graph, int[] colors)
	{
//		System.out.println(RGBDistance(new Color(134, 166, 65).getRGB(),new Color(188, 211, 138).getRGB()));
//		System.out.println(RGBDistance(new Color(134, 166, 65).getRGB(),new Color(166, 115, 65).getRGB()));
		
    List<Point>[] out = new ArrayList[colors.length-1];
    for (int i=0; i<out.length; i++)
      out[i] = new ArrayList<>();
        
    CrossBitmap bit = new CrossBitmap(CrossBitmap.newFrom(graph));
    
		int color; 
    List<Integer> base;
		
    for (int i = 0; i<out.length; i++)
    {
      color = colors[i+1];
      base = generateColorToWhite(color);
      
      for (int y = 0; y< graph.getHeight(); y++)
        for (int x = 0; x< graph.getWidth(); x++)
        {
          if (RGBDistance(color, graph.getRGB(x, y))<50 || ColorToWhite(graph.getRGB(x, y), base) )
            bit.setRGB(x, y, Colors.BLACK);
          else
            bit.setRGB(x, y, Colors.WHITE);
        }
      
      bit = doKMM(bit);
      
      for (int x = 0; x< bit.getWidth(); x++)
        for (int y = 0; y< bit.getHeight(); y++)
          if (bit.getRGB(x, y)==Color.BLACK.getRGB())
          {
            out[i].add(new Point(x,y));
            break;
          }  
    }

		return out;
	}
					

	
//******************************************************
// POMOCNICZE
//******************************************************

	
  
  
  
	public static double RGBDistance(int col1, int col2)
	{
		return Math.sqrt(Math.pow(Colors.getR(col1)-Colors.getR(col2), 2)+Math.pow(Colors.getG(col1)-Colors.getG(col2), 2)+Math.pow(Colors.getB(col1)-Colors.getB(col2), 2));
	}	
  
	public static List<Integer> generateColorToWhite(int base)
	{
    List<Integer> out = new ArrayList<>();
    int r,g,b;
    for (int i=0; i<80; i++)
    {
      r = (int)(((255-Colors.getR(base)) * (double)i/100.0) + Colors.getR(base)); 	     
      g = (int)(((255-Colors.getG(base)) * (double)i/100.0) + Colors.getG(base)); 	     
      b = (int)(((255-Colors.getB(base)) * (double)i/100.0) + Colors.getB(base)); 
      out.add(Colors.RGB(r, g, b));
    }
	
		return out;
  }
	  
  
	public static boolean ColorToWhite(int tested, List<Integer> base)
	{ 
    for (Integer baseColor: base)
      if (RGBDistance(baseColor, tested)<10)
        return true;

    return false;
  }
	
		 
	public static int[] legendAnalize(CrossBitmap legend)
	{ 
    OCRPlugin temp = new OCRPlugin(legend);
    temp.extractText();
    System.out.println(temp.getText());

    return null;
  }
	
	
	
	
	private final static int [] wycieciaA = {3, 5, 7, 12, 13, 14, 15, 20,
											21, 22, 23, 28, 29, 30, 31, 48,
											52, 53, 54, 55, 56, 60, 61, 62,
											63, 65, 67, 69, 71, 77, 79, 80,
											81, 83, 84, 85, 86, 87, 88, 89,
											91, 92, 93, 94, 95, 97, 99, 101,
											103, 109, 111, 112, 113, 115, 116, 117,
											118, 119, 120, 121, 123, 124, 125, 126,
											127, 131, 133, 135, 141, 143, 149, 151,
											157, 159, 181, 183, 189, 191, 192, 193,
											195, 197, 199, 205, 207, 208, 209, 211,
											212, 213, 214, 215, 216, 217, 219, 220,
											221, 222, 223, 224, 225, 227, 229, 231,
											237, 239, 240, 241, 243, 244, 245, 246,
											247, 248, 249, 251, 252, 253, 254, 255};

	private final static int [] czworkiA = {3, 6, 7, 12, 14, 15, 24, 28,
											30, 48, 56, 60, 96, 112, 120, 129,
											131, 135, 192, 193, 195, 224, 225, 240};

	public static int waga(int [][] img, int i, int j)
	{
			int tmp=0;
			if (img[i][j-1]>0) tmp+=1;
			if (img[i+1][j-1]>0) tmp+=2; 
			if (img[i+1][j]>0) tmp+=4;
			if (img[i+1][j+1]>0) tmp+=8;
			if (img[i][j+1]>0) tmp+=16;
			if (img[i-1][j+1]>0) tmp+=32;
			if (img[i-1][j]>0) tmp+=64;
			if (img[i-1][j-1]>0) tmp+=128;
			return tmp;
	}

	public static boolean ishere(int w, int [] tab)
	{   
			for (int i=0; i<tab.length; i++)
					if (tab[i]==w)
							return true;
			return false; 
	}
	
	//Scienanie KMM
	public static CrossBitmap doKMM(CrossBitmap in)
	{
		int width = in.getWidth();
		int height = in.getHeight();

		int [][] img = new int[width+1][height+1];
		int [][] img_copy = new int[width+1][height+1];
    
    final int BLACK = new Color(0,0,0).getRGB();

		CrossBitmap out = new CrossBitmap(CrossBitmap.newFrom(in));

		for(int i = 0; i < width;i++)
			for(int j = 0;j < height;j++)  
				if (in.getRGB(i, j) == BLACK)
				{ 
					img[i+1][j+1]=1;
					img_copy[i+1][j+1]=img[i+1][j+1];               
				}

		//****************************************
		boolean zmiany = true;

		while(zmiany==true)
		{
			zmiany = false;

			for(int i = 1; i<width; i++)
					for(int j = 1; j<height; j++)                 
							if (img[i][j]==1)
									if (img[i-1][j]==0 || img[i+1][j]==0 || 
											img[i][j-1]==0 || img[i][j+1]==0)
											img[i][j]=2; 
									else
									if (img[i-1][j-1]==0 || img[i+1][j-1]==0 ||
											img[i+1][j+1]==0 || img[i-1][j+1]==0)
											img[i][j]=3;  

			 for(int i = 1; i<width; i++)
					for(int j = 1; j<height; j++)   
							if (img[i][j]==2)
									if (ishere(waga(img,i,j),czworkiA))
											img[i][j]=4;         

			 for(int i = 1; i<width; i++)
					for(int j = 1; j<height; j++)                 
							if ((img[i][j]==4))
									if (ishere(waga(img,i,j),wycieciaA))
											img[i][j]=0;
									else
											img[i][j]=1; 

			 for(int i = 1; i<width; i++)
					for(int j = 1; j<height; j++)                 
							if (img[i][j]==2)
									if (ishere(waga(img,i,j),wycieciaA))
											img[i][j]=0;
									else
											img[i][j]=1;            

			 for(int i = 1; i<width; i++)
					for(int j = 1; j<height; j++)                 
							if (img[i][j]==3)
									if (ishere(waga(img,i,j),wycieciaA))
											img[i][j]=0; 
									else
											img[i][j]=1; 

		 //Sprawdzanie zmian  
			 for(int i = 0; i<=width; i++)
					for(int j = 0; j<=height; j++) 
					{
							if(img_copy[i][j] != img[i][j]) 
									zmiany=true;
							img_copy[i][j]=img[i][j];
					}
		}

		for(int i = 0; i < width;i++)
			for(int j = 0;j < height;j++)
				if ((img[i+1][j+1]==0))
					out.setRGB(i, j, Colors.WHITE);
				else
					out.setRGB(i, j, Colors.BLACK);							

		return out;
	}
	
	
}
