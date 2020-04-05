## 数组:

### 初始化

1. 一维数组初始化:

   ```java
   int[] a1 = new int[] { 1, 2, 3 };

   int[] a2 = { 1, 2, 3 };

   int[] a3 = new int[3];
   for (int a : a3) {
       a = 0;
   }
   ```

2. 二维数组:

- 是一个数组, 但每一个元素都是一个一维数组
- for 循环+双下标

  ```java
  public void two_dimensional_array() {
      int[][] a = new int[5][]; // 5行
      for (int i = 0; i < a.length; i++) { // a.length表示行数
          a[i] = new int[i + 1]; // 元素=new数组
          for (int j = 0; j < a[i].length; j++) {
              a[i][j] = i * j + 10;
          }
      }
      // 遍历
      for (int i = 0; i < a.length; i++) { // a.length表示行数
          for (int j = 0; j < a[i].length; j++) {
              System.out.print(a[i][j]+"\t");
          }
          System.out.println();
      }
  }
  ```

3. ArrayList:

   ```java
   private List<Customer>customers = new ArrayList<Customer>();
   customers.get(i); //按下标索引
   return customers.set(i, new Customer("5", "5")); //按下标 set
   ```

### 数组排序

- 基本方法

  ```java
  Collections.sort(List<>);
  Collections.sort(List<>, Comparator);
  Arrays.sort(int[])
  Arrays.asList()
  ```

- 自定义的排序 List: comparator 方法
  ```java
  List<Integer> list02 = new ArrayList<>(...);
  // 自定义 Comparator 对象, 自定义排序
  // 功能: 降序;
  // 最好使用 compareTo 方法
  Comparator<Integer> c = new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
          return o2.compareTo(o1);
      }
  };
  Collections.sort(list02, c);
  ```

---

### sample

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.junit.Test;

public class Array {

    @Test
    // 4. 打印杨辉三角
    public void printTriangle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要输出的行数: ");
        int n = scanner.nextInt();
        scanner.close();
        int [][]a=new int [n][];
        a[0]=new  int[]{1};
        a[1]=new  int[]{1,1};

        for(int i=2;i<n;i++){
            a[i]=new int[i+1];  //a[0]有一个元素
            for(int j=1;j<a[i].length-1;j++){
                a[i][0]=1;
                a[i][a[i].length-1]=1;
                a[i][j]=a[i-1][j-1]+a[i-1][j];
            }
        }

        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length;j++){
                System.out.print(a[i][j]+"\t");
            }
            System.out.println();
        }
    }

    @Test
    // 2. 数组的排序问题: Collections.sort(List<>);
    public void sort_Array_01() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入学生人数: ");
        int n = scanner.nextInt();

        List<Integer> score = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.print("请输入第" + (i + 1) + "名同学的成绩: ");
            score.add(scanner.nextInt());
        }
        scanner.close();

        Collections.sort(score);
        Arrays.sort(score);
    }
}
```
