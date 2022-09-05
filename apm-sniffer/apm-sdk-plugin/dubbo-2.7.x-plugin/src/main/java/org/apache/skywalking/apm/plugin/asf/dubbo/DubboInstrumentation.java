/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugin.asf.dubbo;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 插件定义，继承 xxxPluginDefine，通常命名为 xxxInstrumentation
 * 一般情况下：
 * - 拦截实例方法/构造器时，继承 ClassInstanceMethodsEnhancePluginDefine
 * - 拦截静态方法时，继承 ClassStaticMethodsEnhancePluginDefine
 * AbstractClassEnhancePluginDefine 是所有插件定义的顶级父类
 */
public class DubboInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "org.apache.dubbo.monitor.support.MonitorFilter";

    private static final String INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.asf.dubbo.DubboInterceptor";

    /**
     * 指定插件增强哪个类的字节码
     * <p>
     * ClassMatch 是一个标志接口，表示类的匹配器
     * NameMatch 是通过完整类名精准匹配对应的类
     * 除此之外，比较常用的还有 IndirectMatch 用于间接匹配
     * @see org.apache.skywalking.apm.agent.core.plugin.match.IndirectMatch
     * </p>
     */
    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    /**
     * 拿到构造方法的拦截点
     */
    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    /**
     * 拿到实例方法的拦截点
     */
    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                /**
                 * 拦截类的具体方法过滤器
                 */
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("invoke");
                }

                /**
                 * 字节码增强具体类
                 */
                @Override
                public String getMethodsInterceptor() {
                    return INTERCEPT_CLASS;
                }

                /**
                 * 在字节码增强的过程中，是否要对原方法的入参进行改变
                 */
                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
