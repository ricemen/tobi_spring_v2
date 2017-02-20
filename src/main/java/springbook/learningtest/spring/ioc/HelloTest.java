package springbook.learningtest.spring.ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import springbook.learningtest.spring.ioc.bean.AnnotatedHello;
import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.bean.StringPrinter;
import springbook.learningtest.spring.ioc.config.AnnotatedHelloConfig;

public class HelloTest {
	
	@Test
	public void configurationTest() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
		AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
		assertThat(hello, is(notNullValue()));
		
		AnnotatedHelloConfig config = ctx.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
		assertThat(config, is(notNullValue()));
	}
	
	@Test
	public void simpleBeanScanning() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext("springbook.learningtest.spring.ioc.bean");
		AnnotatedHello hello = ctx.getBean("myAnnotatedHello", AnnotatedHello.class);
		assertThat(hello, is(notNullValue()));
	}
	
	@Test
	public void childAndParent() {
		
		// 부모 context
		String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) +"/";
		ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");
		
		// 자식 context
		GenericApplicationContext child = new GenericApplicationContext(parent);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions(basePath + "childContext.xml");
		child.refresh();
		
		Printer printer = child.getBean("printer", Printer.class);
		assertThat(printer, is(notNullValue()));
		
		Hello hello = child.getBean("hello", Hello.class);
		assertThat(hello, is(notNullValue()));
		hello.print();
		assertThat(printer.toString(), is("Hello Child"));
	}
	
	@Test
	/**
	 * GenericApplicationContext 사용
	 */
	public void genericApplicationContext() {
		// default 생성방식 
//		GenericApplicationContext ac = new GenericApplicationContext();
//		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
//		reader.loadBeanDefinitions("springbook/learningtest/spring/ioc/genericApplicationContext.xml");
		
//		ac.refresh(); // 초기화 명령
		
		// GenericXmlApplicationContext - GenericApplicationContext + XmlBeanDefinitionReader 를 이용한 방식
		// xml 로딩 및 ac.refresh() - 초기화 까지 같이 해준다.
		GenericXmlApplicationContext ac = new GenericXmlApplicationContext("springbook/learningtest/spring/ioc/genericApplicationContext.xml");
		
		
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
		
	}
	
	@Test
	/**
	 * StaticApplicationContext 사용
	 */
	public void registerBeanWithDependency() {
		
		StaticApplicationContext ac = new StaticApplicationContext();	// code를 통해 bean 메타 정보를 등록할때 사용한다. 실무에서는 X 
		
		
		ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));	// <bean id="printer" class="springbook.learningtest.spring.ioc.StringPrinter" /> 와 동일
		
		// Hello bean 설정(프로퍼티) 및 빈 등록
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring"); // Property 단순 값 등록
		helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));	// Property printer 에 printer 빈 등록
		
		ac.registerBeanDefinition("hello", helloDef);
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

	@Test
	public void helloTest() {
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.registerSingleton("hello1", Hello.class);
		
		Hello hello1 = ac.getBean("hello1", Hello.class);
		assertThat(hello1, is(notNullValue()));
	}
	
}
