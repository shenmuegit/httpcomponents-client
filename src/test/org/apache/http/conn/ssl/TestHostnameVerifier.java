/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.conn.ssl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.net.ssl.SSLException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Unit tests for {@link HostnameVerifier}.
 *
 * @author Julius Davies
 * @since 11-Dec-2006
 */
public class TestHostnameVerifier extends TestCase
      implements CertificatesToPlayWith {

    public TestHostnameVerifier(String testName) {
        super(testName);
    }

    public static void main(String args[]) throws Exception {
        String[] testCaseName = { TestHostnameVerifier.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public static Test suite() {
        TestSuite ts = new TestSuite();
        ts.addTestSuite(TestHostnameVerifier.class);
        return ts;
    }

    public void testVerify() throws Exception {
        HostnameVerifier DEFAULT = HostnameVerifier.DEFAULT;
        HostnameVerifier STRICT = HostnameVerifier.STRICT;
        HostnameVerifier ALLOW_ALL = HostnameVerifier.ALLOW_ALL;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream in;
        X509Certificate x509;
        in = new ByteArrayInputStream(X509_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);

        DEFAULT.verify("foo.com", x509);
        STRICT.verify("foo.com", x509);
        exceptionPlease(DEFAULT, "a.foo.com", x509);
        exceptionPlease(STRICT, "a.foo.com", x509);
        exceptionPlease(DEFAULT, "bar.com", x509);
        exceptionPlease(STRICT, "bar.com", x509);
        ALLOW_ALL.verify("foo.com", x509);
        ALLOW_ALL.verify("a.foo.com", x509);
        ALLOW_ALL.verify("bar.com", x509);

        in = new ByteArrayInputStream(X509_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        DEFAULT.verify("\u82b1\u5b50.co.jp", x509);
        STRICT.verify("\u82b1\u5b50.co.jp", x509);
        exceptionPlease(DEFAULT, "a.\u82b1\u5b50.co.jp", x509);
        exceptionPlease(STRICT, "a.\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(X509_FOO_BAR);
        x509 = (X509Certificate) cf.generateCertificate(in);
        DEFAULT.verify("foo.com", x509);
        STRICT.verify("foo.com", x509);
        exceptionPlease(DEFAULT, "a.foo.com", x509);
        exceptionPlease(STRICT, "a.foo.com", x509);
        DEFAULT.verify("bar.com", x509);
        STRICT.verify("bar.com", x509);
        exceptionPlease(DEFAULT, "a.bar.com", x509);
        exceptionPlease(STRICT, "a.bar.com", x509);

        in = new ByteArrayInputStream(X509_FOO_BAR_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        DEFAULT.verify("foo.com", x509);
        STRICT.verify("foo.com", x509);
        exceptionPlease(DEFAULT, "a.foo.com", x509);
        exceptionPlease(STRICT, "a.foo.com", x509);
        DEFAULT.verify("bar.com", x509);
        STRICT.verify("bar.com", x509);
        exceptionPlease(DEFAULT, "a.bar.com", x509);
        exceptionPlease(STRICT, "a.bar.com", x509);

        /*
           Java isn't extracting international subjectAlts properly.  (Or
           OpenSSL isn't storing them properly).
        */
        // DEFAULT.verify("\u82b1\u5b50.co.jp", x509 );
        // STRICT.verify("\u82b1\u5b50.co.jp", x509 );
        exceptionPlease(DEFAULT, "a.\u82b1\u5b50.co.jp", x509);
        exceptionPlease(STRICT, "a.\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(X509_NO_CNS_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        DEFAULT.verify("foo.com", x509);
        STRICT.verify("foo.com", x509);
        exceptionPlease(DEFAULT, "a.foo.com", x509);
        exceptionPlease(STRICT, "a.foo.com", x509);

        in = new ByteArrayInputStream(X509_NO_CNS_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        DEFAULT.verify("foo.com", x509);
        STRICT.verify("foo.com", x509);
        exceptionPlease(DEFAULT, "a.foo.com", x509);
        exceptionPlease(STRICT, "a.foo.com", x509);

        in = new ByteArrayInputStream(X509_THREE_CNS_FOO_BAR_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        exceptionPlease(DEFAULT, "foo.com", x509);
        exceptionPlease(STRICT, "foo.com", x509);
        exceptionPlease(DEFAULT, "a.foo.com", x509);
        exceptionPlease(STRICT, "a.foo.com", x509);
        exceptionPlease(DEFAULT, "bar.com", x509);
        exceptionPlease(STRICT, "bar.com", x509);
        exceptionPlease(DEFAULT, "a.bar.com", x509);
        exceptionPlease(STRICT, "a.bar.com", x509);
        DEFAULT.verify("\u82b1\u5b50.co.jp", x509);
        STRICT.verify("\u82b1\u5b50.co.jp", x509);
        exceptionPlease(DEFAULT, "a.\u82b1\u5b50.co.jp", x509);
        exceptionPlease(STRICT, "a.\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(X509_WILD_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        exceptionPlease(DEFAULT, "foo.com", x509);
        exceptionPlease(STRICT, "foo.com", x509);
        DEFAULT.verify("www.foo.com", x509);
        STRICT.verify("www.foo.com", x509);
        DEFAULT.verify("\u82b1\u5b50.foo.com", x509);
        STRICT.verify("\u82b1\u5b50.foo.com", x509);
        DEFAULT.verify("a.b.foo.com", x509);
        exceptionPlease(STRICT, "a.b.foo.com", x509);

        in = new ByteArrayInputStream(X509_WILD_CO_JP);
        x509 = (X509Certificate) cf.generateCertificate(in);
        // Silly test because no-one would ever be able to lookup an IP address
        // using "*.co.jp".
        DEFAULT.verify("*.co.jp", x509);
        STRICT.verify("*.co.jp", x509);
        exceptionPlease(DEFAULT, "foo.co.jp", x509);
        exceptionPlease(STRICT, "foo.co.jp", x509);
        exceptionPlease(DEFAULT, "\u82b1\u5b50.co.jp", x509);
        exceptionPlease(STRICT, "\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(X509_WILD_FOO_BAR_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        // try the foo.com variations
        exceptionPlease(DEFAULT, "foo.com", x509);
        exceptionPlease(STRICT, "foo.com", x509);
        DEFAULT.verify("www.foo.com", x509);
        STRICT.verify("www.foo.com", x509);
        DEFAULT.verify("\u82b1\u5b50.foo.com", x509);
        STRICT.verify("\u82b1\u5b50.foo.com", x509);
        DEFAULT.verify("a.b.foo.com", x509);
        exceptionPlease(STRICT, "a.b.foo.com", x509);
        // try the bar.com variations
        exceptionPlease(DEFAULT, "bar.com", x509);
        exceptionPlease(STRICT, "bar.com", x509);
        DEFAULT.verify("www.bar.com", x509);
        STRICT.verify("www.bar.com", x509);
        DEFAULT.verify("\u82b1\u5b50.bar.com", x509);
        STRICT.verify("\u82b1\u5b50.bar.com", x509);
        DEFAULT.verify("a.b.bar.com", x509);
        exceptionPlease(STRICT, "a.b.bar.com", x509);
        // try the \u82b1\u5b50.co.jp variations
        /*
           Java isn't extracting international subjectAlts properly.  (Or
           OpenSSL isn't storing them properly).
        */
        //exceptionPlease( DEFAULT, "\u82b1\u5b50.co.jp", x509 );
        //exceptionPlease( STRICT, "\u82b1\u5b50.co.jp", x509 );
        //DEFAULT.verify("www.\u82b1\u5b50.co.jp", x509 );
        //STRICT.verify("www.\u82b1\u5b50.co.jp", x509 );
        //DEFAULT.verify("\u82b1\u5b50.\u82b1\u5b50.co.jp", x509 );
        //STRICT.verify("\u82b1\u5b50.\u82b1\u5b50.co.jp", x509 );
        //DEFAULT.verify("a.b.\u82b1\u5b50.co.jp", x509 );
        //exceptionPlease(STRICT,"a.b.\u82b1\u5b50.co.jp", x509 );
    }

    private void exceptionPlease(HostnameVerifier hv, String host,
                                 X509Certificate x509) {
        try {
            hv.verify(host, x509);
            fail("HostnameVerifier shouldn't allow [" + host + "]");
        }
        catch(SSLException e) {
            // whew!  we're okay!
        }
    }

}
