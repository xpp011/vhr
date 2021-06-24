# vhr
微人事项目练习;
原作者:江南一点雨;
地址:https://github.com/lenve/vhr






# 微人事项目笔记










## 后端



### JSON登录

扩展`UsernamePasswordAuthenticationFilter`加入JSON登录遇到的问题

在未接入前后端分离时，如果需要JSON登录的话，我们只需要扩展`UsernamePasswordAuthenticationFilter`的`attemptAuthentication`方法,像这样


![image-20210615003141425](https://user-images.githubusercontent.com/72737308/123312637-31d95900-d55b-11eb-8e3a-c116bc20d7eb.png)


在`attemptAuthentication`方法中判断了当前请求头是否为JSON数据，如果是，我们则通过io流的方式读取JSON数据，并映射成Map类型。



最后我们需要将扩展好的类替换掉原来`UsernamePasswordAuthenticationFilter`类的`attemptAuthentication`方法,这样才能使我们的方法奏效，像这样

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
 ...
        //添加自定义过滤器到UsernamePasswordAuthenticationFilter
        http.addFilterAt(myAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class);
    }

    //将自定义的登录过滤器创建
    MyUsernamePasswordAuthenticationFilter myAuthenticationFilter() throws Exception {
        MyUsernamePasswordAuthenticationFilter filter = new MyUsernamePasswordAuthenticationFilter();
        //设置权限管理
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }
}
```



所有配置完成了以后，在未接入前后端分离的项目的时候就可以使用JSON登录了







而我在VUE这个前后端分离项目使用，我需要将登录成功信息，登录失败信息以JSON数据返回出去,

如果像我上面这样配置的话，会发现Security怎么也不返回登录失败或者成功的JSON信息，同时将请求转跳到`/`路径

即使我在`successHandler`和`failureHandler`方法中创建配置了处理器

<img src="D:\win10桌面存放\项目\MyVhr\微人事项目笔记.assets\image-20210615004037941.png" alt="image-20210615004037941" style="zoom:50%;" />



**这是为什么呢**

我明明在`successHandler`和`failureHandler`方法中配置了处理器，而它就是不返回JSON数据，同时还将我重定向到`/`路径

我们都知道重定向到`/`路径是Security默认的登录成功处理方式

从这个点出发，就可以联想到，是不是我的处理器没有在我们自定义继承`UsernamePasswordAuthenticationFilter`

的类中生效呢

突然想一想，我们自定义继承`UsernamePasswordAuthenticationFilter`的类也只是扩展了`attemptAuthentication`方法，并没有去重新设置他的登录成功处理器和登录失败处理器,所以我们的Security始终不返回JSON数据，而是走默认的处理器，将其登录成功的用户重定向到`/`路径



好的，现在我们知道问题所在了，那么我们该如何配置呢?



**自定义扩展类**<font color='red'>SecurityJsonLoginConfig</font>

在该类中由于我需要重定义处理登录的路径改为`doLogin`

所以我们直接重写`UsernamePasswordAuthenticationFilter`类的父类`AbstractAuthenticationProcessingFilter`

并设置DEFAULT_ANT_PATH_REQUEST_MATCHER为`/doLogin`

![image-20210615005308404](https://user-images.githubusercontent.com/72737308/123312771-5af9e980-d55b-11eb-8640-b2f3e215adb7.png)
```java
//修改Security支持Json登录
//该配置类无需通过@Configuration给Spring托管 通过@Bean托管  还需要配置授权管理setAuthenticationManager方法
public class SecurityJsonLoginConfig extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";

    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/doLogin",
            "POST");

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;

    private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;

    private boolean postOnly = true;

    public SecurityJsonLoginConfig() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public SecurityJsonLoginConfig(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }


    //重写UsernamePasswordAuthenticationFilter类中的attemptAuthentication
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        System.out.println("判断JSON");
        Map<String,String> map;
        UsernamePasswordAuthenticationToken authRequest;
        String username,password;

        //判断请求是否为JSON格式
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){//从JSON中拿去数据
            try {
                //通过IO流的形式得到数据
                map=new ObjectMapper().readValue(request.getInputStream(),Map.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new AuthenticationServiceException("JSON登录信息解析失败");
            }
            username = map.get("username");
            username = (username != null) ? username : "";
            username = username.trim();
            password = map.get("password");
            password = (password != null) ? password : "";
            System.out.println(username+" : "+password);
            // Allow subclasses to set the "details" property
        }else {//源码
            username = obtainUsername(request);
            username = (username != null) ? username : "";
            username = username.trim();
            password = obtainPassword(request);
            password = (password != null) ? password : "";
        }
        authRequest = new UsernamePasswordAuthenticationToken(username, password);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);

    }



    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }

    /**
     * Enables subclasses to override the composition of the username, such as by
     * including additional values and a separator.
     * @param request so that request attributes can be retrieved
     * @return the username that will be presented in the <code>Authentication</code>
     * request token to the <code>AuthenticationManager</code>
     */
    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }

    /**
     * Provided so that subclasses may configure what is put into the authentication
     * request's details property.
     * @param request that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details
     * set
     */
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    /**
     * Sets the parameter name which will be used to obtain the username from the login
     * request.
     * @param usernameParameter the parameter name. Defaults to "username".
     */
    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    /**
     * Sets the parameter name which will be used to obtain the password from the login
     * request..
     * @param passwordParameter the parameter name. Defaults to "password".
     */
    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter. If set to
     * true, and an authentication request is received which is not a POST request, an
     * exception will be raised immediately and authentication will not be attempted. The
     * <tt>unsuccessfulAuthentication()</tt> method will be called as if handling a failed
     * authentication.
     * <p>
     * Defaults to <tt>true</tt> but may be overridden by subclasses.
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return this.usernameParameter;
    }

    public final String getPasswordParameter() {
        return this.passwordParameter;
    }
}

```





**Security配置类**<font color='red'>SecurityConfig</font>

由于我们登录成功处理器和登录失败处理器需要设置两个地方

一个是formLogin方法![image-20210615005531170](https://user-images.githubusercontent.com/72737308/123312841-6fd67d00-d55b-11eb-924b-8bb76dc86416.png)





还有一个是自定义类设置处理器![image-20210615005724745](https://user-images.githubusercontent.com/72737308/123312867-77962180-d55b-11eb-8097-317ef4de319e.png)





```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    HrService hrService;

    //密码编码
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().anyRequest().authenticated()
        .and()
        .formLogin().loginPage("/toLogin").loginProcessingUrl("doLogin")
             //登录成功处理器
            .successHandler(myAuthenticationSuccessHandler())
             //登录失败处理器
            .failureHandler(myAuthenticationFailureHandler()).permitAll()
        .and()
        .logout().logoutUrl("/Logout").logoutSuccessHandler(new LogoutSuccessHandler() {//退出成功处理
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                Hr hr = (Hr) authentication.getPrincipal();
                ResponseBean ok = ResponseBean.ok("用户"+hr.getUsername()+"退出成功");
                String s = new ObjectMapper().writeValueAsString(ok);
                out.write(s);

                out.flush();
                out.close();
            }
        }).permitAll()
        .and()
        .csrf().disable();

        http.addFilterAt(securityJsonLoginConfig(),UsernamePasswordAuthenticationFilter.class);
    }

    //SecurityJson登录的配置类
    @Bean
    SecurityJsonLoginConfig securityJsonLoginConfig() throws Exception {
        SecurityJsonLoginConfig filter = new SecurityJsonLoginConfig();
        //将自定义设置类设置登录成功处理器
        filter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler());
        //将自定义设置类设置登录失败处理器
        filter.setAuthenticationFailureHandler(myAuthenticationFailureHandler());
        //设置权限管理
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }


    //登录成功的处理器
    AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new AuthenticationSuccessHandler() {//登录成功处理
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                //getPrincipal  获取当前用户信息
                Hr hr = (Hr) authentication.getPrincipal();
                //返回前端时清除password
                hr.setPassword(null);
                ResponseBean ok = ResponseBean.ok("登录成功",hr);

                String s = new ObjectMapper().writeValueAsString(ok);

                out.write(s);

                out.flush();
                out.close();
            }
        };
    }

    //登录失败的处理器
    AuthenticationFailureHandler myAuthenticationFailureHandler(){
        return new AuthenticationFailureHandler() {//登录失败处理
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                //getPrincipal  获取当前用户信息
                ResponseBean error = ResponseBean.error("登录失败");

                if (exception instanceof BadCredentialsException){
                    error.setMsg("用户名或者密码错误");
                }else if (exception instanceof UsernameNotFoundException){
                    error.setMsg("用户名不存在");
                }else if (exception instanceof AccountExpiredException){
                    error.setMsg("账户被锁定");
                }else if (exception instanceof LockedException){
                    error.setMsg("账户被锁定");
                }else if (exception instanceof DisabledException){
                    error.setMsg("账户不可用");
                }else if (exception instanceof CredentialsExpiredException){
                    error.setMsg("证书过期");
                }

                String s = new ObjectMapper().writeValueAsString(error);

                out.write(s);

                out.flush();
                out.close();
            }
        };
    }
}

```







### POI

#### 使用POI实现表单的下载

首先导入Maven依赖

```Java
<!-- https://mvnrepository.com/artifact/org.apache.poi/poi -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.0.0</version>
</dependency>
```



大致思路

基本操作就是  创建一个标题列  设置抬头

设置完成后

枚举从数据库中查询到的实体类集合

将数值一一映射到对应的列

映射完成后

将数据写到一个byte的数组中写出，设置响应头，文件名称等等



`ResponseEntity`<font color='red'>可以作为controller的返回值，比如对于一个处理下载二进制文件的接口</font>

```java
public class POIUtil {

    static String[] titles={"姓名","性别","工号","出生日期","身份证号码","婚姻状况","民族","籍贯","政治面貌","电子邮件","联系地址","所属部门","职位","聘用形式","入职日期","转正日期","合同起始日期","合同终止日期","合同期限","毕业院校","专业","最高学历"};

    public static ResponseEntity<byte[]> employeeExcel(List<Employee> list){
        //1，创建一个Excel文档
        HSSFWorkbook workbook = new HSSFWorkbook();

        //2，创建文档的摘要
        workbook.createInformationProperties();

        //3，获取文档信息并配置
        DocumentSummaryInformation docInfo = workbook.getDocumentSummaryInformation();

        //文档类别
        docInfo.setCategory("员工信息");

        //文档管理员
        docInfo.setManager("xpp011");
        //公司信息
        docInfo.setCompany("www.xpp011.cn");

        //4，获取文档摘要信息
        SummaryInformation summInfo = workbook.getSummaryInformation();

        //文档标题
        summInfo.setTitle("员工信息表");

        //文档作者
        summInfo.setAuthor("xpp011");

        //文档备注
        summInfo.setComments("本文档由xpp011提供");

        //创建标题行样式
        HSSFCellStyle headerStyle=workbook.createCellStyle();
        //设置背景色为黄色
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        //前景色
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //设置有日期的单元格的日期格式
        HSSFCellStyle dateFormat=workbook.createCellStyle();
        dateFormat.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
        //创建一张表
        HSSFSheet sheet = workbook.createSheet("员工信息表");



        //6,在表内创建一行  第一行为标题行
        HSSFRow r0 = sheet.createRow(0);
        //创建列  循环遍历titles设立抬头
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = r0.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(titles[i]);
            //设置表格单元格大小
            sheet.setColumnWidth(i,25*256);
        }

        //开始插入数据
        for (int i = 0; i < list.size(); i++) {
            Employee emp=list.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(emp.getName());
            row.createCell(1).setCellValue(emp.getGender());
            row.createCell(2).setCellValue(emp.getWorkID());
            //出生日期设置日期格式
            HSSFCell cell3 = row.createCell(3);
            cell3.setCellStyle(dateFormat);
            cell3.setCellValue(emp.getBirthday());

            row.createCell(4).setCellValue(emp.getIdCard());
            row.createCell(5).setCellValue(emp.getWedlock());
            row.createCell(6).setCellValue(emp.getNation().getName());
            row.createCell(7).setCellValue(emp.getNativePlace());
            row.createCell(8).setCellValue(emp.getPoliticsstatus().getName());
            row.createCell(9).setCellValue(emp.getEmail());
            row.createCell(10).setCellValue(emp.getAddress());
            row.createCell(11).setCellValue(emp.getDepartment().getName());
            row.createCell(12).setCellValue(emp.getPosition().getName());
            row.createCell(13).setCellValue(emp.getEngageForm());
            //设置入职日期
            HSSFCell cell14 = row.createCell(14);
            cell14.setCellStyle(dateFormat);
            cell14.setCellValue(emp.getBeginDate());

            //设置转正日期
            HSSFCell cell15 = row.createCell(15);
            cell15.setCellStyle(dateFormat);
            cell15.setCellValue(emp.getConversionTime());

            //合同起始日期
            HSSFCell cell16 = row.createCell(16);
            cell16.setCellStyle(dateFormat);
            cell16.setCellValue(emp.getBeginContract());

            //合同终止日期
            HSSFCell cell17 = row.createCell(17);
            cell17.setCellStyle(dateFormat);
            cell17.setCellValue(emp.getEndContract());

            row.createCell(18).setCellValue(emp.getContractTerm());
            row.createCell(19).setCellValue(emp.getSchool());
            row.createCell(20).setCellValue(emp.getSpecialty());
            row.createCell(21).setCellValue(emp.getTiptopDegree());
        }
        //创建一个Byte类型的字节流
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        HttpHeaders header=new HttpHeaders();
        try {
            //设置文件名  并设置编码格式
            header.setContentDispositionFormData("attachment",new String("员工表.xls".getBytes(StandardCharsets.UTF_8),"ISO-8859-1"));
            //设置响应头为文件下载
            header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            //将数据写到输出流中
            workbook.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //传一个byte的数组    设置响应头  设置状态码
        return new ResponseEntity<byte[]>(baos.toByteArray(),header, HttpStatus.CREATED);
    }
    }
```





#### 上传Excel，解析其数据



大致思路

解析Excel表格中各行的数据,对于一些特殊数据需要特殊判断，当然最多的就是String类型数据，但不乏DATA类型，Double类型的数据，对于这些数据需要特殊处理（只是调用一些不同方法），获取Excel中每一行每一列的数据，将其数据映射到对于实体类的属性上即可,解析完成完成后，对实体类集合进行插入操作即可



```java
    public static List<Employee> importEmp (MultipartFile file, List<Nation> nations, List<Politicsstatus> politicsstatus, List<Department> departmentAll, List<Position> positionAll, List<JobLevel> jobLevelAll) throws Exception{
        List<Employee> list=new ArrayList<>();
        Employee employee=null;
        //1,创建WorkBook对象
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());

        //2,获取表格数量
        int numberOfSheets = workbook.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            //3,获取表单
            HSSFSheet sheetAt = workbook.getSheetAt(i);

            //4，获取表单中的行数
            int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();

            //跳过标题行
            for (int j = 1; j < physicalNumberOfRows; j++) {
                //5,获取数据行
                HSSFRow row = sheetAt.getRow(j);
                //判断是否为空行
                if (row==null){
                    continue;
                }

                //6,获取列数
                //初始化Employee
                employee=new Employee();
                int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                for (int k = 0; k < physicalNumberOfCells; k++) {
                    HSSFCell cell = row.getCell(k);
                    //首先匹配字符串类型的数据
                    //日期格式的统一放在最后去处理
                    switch (cell.getCellType()){
                        case STRING:
                            String cellValue = cell.getStringCellValue();
                            switch (k){
                                case 0:
                                    employee.setName(cellValue);//姓名
                                    break;
                                case 1:
                                    employee.setGender(cellValue);//性别
                                    break;
                                case 2:
                                    employee.setWorkID(cellValue);//工号
                                    break;
                                case 4:
                                    employee.setIdCard(cellValue);//身份证号
                                    break;
                                case 5:
                                    employee.setWedlock(cellValue);//婚姻状况
                                    break;
                                case 6:
                                    //重写equals  使其只要name相同则判定两个对象相同
                                    //使用IndexOf方法时就会寻找名字相同的对象返回它的Index
                                    int natsIndex = nations.indexOf(new Nation(cellValue));
                                    //通过get方法得到对应的Nation 并获取它的ID
                                    employee.setNationId(nations.get(natsIndex).getId());//民族id
                                    break;
                                case 7:
                                    employee.setNativePlace(cellValue);//籍贯
                                    break;
                                case 8:
                                    //如法炮制
                                    int polIndex = politicsstatus.indexOf(new Politicsstatus(cellValue));
                                    employee.setPoliticId(politicsstatus.get(polIndex).getId());//政治面貌
                                    break;
                                case 9:
                                    employee.setEmail(cellValue);
                                    break;
                                case 10:
                                    employee.setAddress(cellValue);
                                    break;
                                case 11:
                                    int depIndex = departmentAll.indexOf(new Department(cellValue));
                                    employee.setDepartmentId(departmentAll.get(depIndex).getId());//所属部门
                                    break;
                                case 12:
                                    int posIndex = positionAll.indexOf(new Position(cellValue));
                                    employee.setPosId(positionAll.get(posIndex).getId());//职位
                                    break;
                                case 13:
                                    employee.setEngageForm(cellValue);//聘用形式
                                    break;
                                case 19:
                                    employee.setSchool(cellValue);//毕业院校
                                    break;
                                case 20:
                                    employee.setSpecialty(cellValue);//专业
                                    break;
                                case 21:
                                    employee.setTiptopDegree(cellValue);//最高学历
                                    break;
                                case 22:
                                    int jobIndex = jobLevelAll.indexOf(new JobLevel(cellValue));
                                    employee.setJobLevelId(jobLevelAll.get(jobIndex).getId());//职称

                            }
                        default:{
                            //日期特殊处理 getDateCellValue方法得到日期
                            switch (k){
                                case 3:
                                    employee.setBirthday(cell.getDateCellValue());//出生日期
                                    break;
                                case 14:
                                    employee.setBeginDate(cell.getDateCellValue());//入职日期
                                    break;
                                case 15:
                                    employee.setConversionTime(cell.getDateCellValue());//转正日期
                                    break;
                                case 16:
                                    employee.setBeginContract(cell.getDateCellValue());//合同起始日期
                                    break;
                                case 17:
                                    employee.setEndContract(cell.getDateCellValue());//合同终止日期
                                    break;
                                //非日期格式  double格式
                                case 18:
                                    employee.setContractTerm(cell.getNumericCellValue());
                                    break;
                            }
                        }
                        break;
                    }
                }
                //将设置好元素的employee添加到list中
                list.add(employee);
            }
        }

        return list;
    }
```







### TemplateEngine 

通过TemplateEngine 和Context 的配合，我们可以使用thymeleaf模版来生产html文件。

![image-20210623002239931](https://user-images.githubusercontent.com/72737308/123312981-91cfff80-d55b-11eb-892b-53fb2afd81fa.png)

![image-20210623002308946](https://user-images.githubusercontent.com/72737308/123313002-998fa400-d55b-11eb-9527-d7bf3d0c1e0c.png)

**Context是给thymeleaf模版提供变量的**。







### WebSocket

首先导入Websocket依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
```







书写WebSocket的配置类

```java
//配置类
@Controller
//启动WebSocket消息代理
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //启用一个webSocket的UEL
        //其他域连接该WebSocket的URL
        //setAllowedOriginPatterns 设置接收其他域的请求  *为所有请求
        registry.addEndpoint("/ws/ep").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //启动一个简单的代理
        registry.enableSimpleBroker("/queue");
    }
}
```





WebSocket的控制器

```java
@Controller
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //收拾消息的URL
    @MessageMapping("/ws/chat")
    //principal当前用户
    public void handleMsg(Principal principal,ChatMsg chatMsg){
        chatMsg.setFrom(principal.getName());
        chatMsg.setDate(new Date());
        //向/queue/chat地址发送消息
        simpMessagingTemplate.convertAndSendToUser(chatMsg.getTo(),"/queue/chat",chatMsg);
    }
}
```







#### 前端

**导入两个WebSocket的js** 

![image-20210624181435448](https://user-images.githubusercontent.com/72737308/123313023-a0b6b200-d55b-11eb-9de4-48cd83c030aa.png)

如果项目报错还需要安装`sockjs-client`





**设置转发代理**

在vue.config.js文件中设置ws协议，对于前缀为/ws路径的都代理到localhost:8081端口去
![image-20210624181650658](https://user-images.githubusercontent.com/72737308/123313040-a57b6600-d55b-11eb-9648-42272b28ef64.png)



**建立连接**

导入接口
![image-20210624181925380](https://user-images.githubusercontent.com/72737308/123313054-aad8b080-d55b-11eb-8826-3c927c69a5b1.png)

![image-20210624181939798](https://user-images.githubusercontent.com/72737308/123313229-e1aec680-d55b-11eb-9e29-e3ef5309f441.png)







**发送消息**
![image-20210624182018822](https://user-images.githubusercontent.com/72737308/123313100-bb892680-d55b-11eb-88e1-4ba35d56b90a.png)
