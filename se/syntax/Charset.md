## CharSet

### sample

```java
package basical;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

// 功能: 测试Charset字符集
public class CharsetTest {
    @Test
    /**
     * 功能: 测试编码解码的问题
     *  1.notice: 这里的flip()的使用: 只有在使用put()z之后采取去掉用, 耳边吗解码这里是不需要哦调用flip()的
     */
    public void testCharset2() {
        try {
            //1.创建码的方式
            Charset cs=Charset.forName("GBK");
            //2.创建Buffer,并放入数据
            CharBuffer charBuffer=CharBuffer.allocate(1024);
            charBuffer.put("张壮壮啧啧啧！".toCharArray());

            //3.创建编码器(编码或是字节流),并CharBuffer中的数据进行编码
             charBuffer.flip();
             CharsetEncoder cer= cs.newEncoder();
             ByteBuffer byteBuffer=cer.encode(charBuffer);
             //4.创建解码器, 并解码
             // System.out.println(byteBuffer);
             CharsetDecoder cdr=cs.newDecoder();
             CharBuffer charBuffer2=cdr.decode(byteBuffer);

             //5.输出解码后的
             System.out.println("dfgwhsjkal;,"+charBuffer2.toString());
        } catch (CharacterCodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    // 打印Charset中有多少种字符集
    public void testCharset() {

        SortedMap<String, Charset> map=Charset.availableCharsets();
        System.out.println("Charset中有"+map.size()+"种字符集");	//168
        for (Map.Entry<String, Charset> iterable_element : map.entrySet()) {
            System.out.println(iterable_element.getKey()+":"+iterable_element.getValue());
        }
    }
}
```
