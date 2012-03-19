package edu.mit.compilers;

class Program
{
  public static int gurp(int a, int b, boolean c, int d, int e, int f, boolean g, int h)
  {
	  
    int i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, a1, a2, a3, a4;
    x = 0;
    s = 0;
    System.out.printf("top of gurp\n");
    j = a * b - e + d;
    System.out.printf("done j\n");
    i = j - 1;
    System.out.printf("done i\n");
    j = 3 + i;
    System.out.printf("done j\n");
    k = i + j - 2;
    System.out.printf("done k\n");
    l = k - j;
    System.out.printf("done k\n");
    m = i + k;
    System.out.printf("done m\n%d %d\n",k,m);
    n = i + (j - (k - m));
    System.out.printf("after n\n");
    
    System.out.printf("jiklmn: %d %d %d %d %d %d\n",j,i,k,l,m,n);
    if ( a > 50 )
      {
	o = a + b + l;
	j = l - m;
	if ( c )
	  {
	    l = e + f - (h + n);
	    f = f - 1;
	    q = o - 3;
	  }
	else
	  {
	    q = o + 3;
	  }
	if ( c || g )
	  {
	    s = f + e + (h - b);
	  }
      }
    else
      {
	o = b - a;
	j = l + m;
	if ( g )
	  {
	    f = 3 + (a - o);
	    q = 3 - o;
	    s = f + q;
	  }
	else
	  {
	    s = 3 + (o - a - 2);
	    q = s + j;
	    f = s + q - q;
	  }
      }
    System.out.printf("after first if\n");
    t = e + l - i;
    a1 = t + j + o;
    r = t - a1 + m - j;
    v = l + a1 + (t - (q + f));
    if ( g )
      {
	w = 3;
      }
    else
      {
	w = -1;
      }
    System.out.printf("after second if\n");
    u = 99 + n - e + t;
    y = e - s + n + o + m - e + b - b + e;
    a2 = a1 + t - n - t;
    a4 = h + f + d - e;
    if ( w == -1 )
      {
	a3 = a2 + a4;
      }
    else
      {
	a3 = a4 - a2;
      }
    z = u - v + w;
    p = 42 - z;

    System.out.printf("after third if\n");
    System.out.printf("%d",a - i + d + j - e - f + l + m);
    System.out.printf(" ");
    System.out.printf("%d",b - k - h + n + o + q - r - a2);
    System.out.printf(" ");
    System.out.printf("\nVALS %d %d %d %d %d %d %d %d\n",a4, s, t, z, y, x, w, a3);
    System.out.printf("\nVALS %d %d %d %d %d %d %d %d\n",z, a3, y, w, x, a4, t, s);
    System.out.printf("%d!",a4 + s - t + z - y + x + w - a3);
    System.out.printf(" ");
    if ( c )
      {
	System.out.printf("%d",p + u - v + a1);
      }
    else
      {
	if ( g )
	  {
	    System.out.printf("%d",u - v + a1 - p);
	  }
	else
	  {
	    System.out.printf("%d",a1 + v + p - u);
	  }
      }
    System.out.printf("\nafter fourth if\n");
    return a - b + d + e + f + h + i + j + k + l + m - n + o - p + q
      - r + s - t + u - v + w - x + y - z + a1 + a2 + a3 - a4;
  }


  public static void main(String[] args)
  {
    int y, z, a, b;
    int x;
    for (x = 3; x<5; x++) 
      {
	System.out.printf("Top of loop\n");
	z = gurp(1 + x, 0-x, true, 3, 2 + x, 1 - x, true, 4 - x);
	System.out.printf("done z gurp\n");
	System.out.printf("%d",x);
	System.out.printf("\n");
	z = gurp(1 + x, 0-x, true, 3, 2 + x, x + 1, true, 0);
	System.out.printf("done 0z gurp\n");
	y = gurp(3 - x, -8 + x, false, 12 - 3 * x, 16 + x, 1, true, 8 - x);
	System.out.printf("done y gurp\n");
	a = gurp(2 - x, 6 + x, false, x * -3, 1, 3 - x * 2, false, 5 - x);
	System.out.printf("done a gurp\n");
	b = gurp(-3, 8, true, 7 - x, x - 4, 2, false, 6 - x * 9);
	System.out.printf("done b gurp\n");
	System.out.printf("After gurps\n");
	System.out.printf("%d",y); 
	System.out.printf(" ");
	System.out.printf("%d",z); 
	System.out.printf(" ");
	System.out.printf("%d",a); 
	System.out.printf(" ");
	System.out.printf("%d",b); 
	System.out.printf("\n");
      }
  }
}