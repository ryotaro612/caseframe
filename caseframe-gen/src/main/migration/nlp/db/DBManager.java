package jp.ac.titech.cs.se.nlp.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * DBとの接続を管理。リソース解放を解放時に自動で行うコネクションをダイナミックプロキシで生成。
 * 
 * @author rtakizawa
 */
public class DBManager {

    // DBファイルがこのパスにあることを仮定
    private static final String dbPath = "res/db/edr.db";

    static {
        try {
            // sqliteのJDBCドライバをロード
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBCライブラリを導入してください", e);
        }
    }

    private Object proxy;
    private Connection connection;
    private Set<Statement> statements = new HashSet<Statement>();
    private Set<ResultSet> resultSets = new HashSet<ResultSet>();

    DBManager(Connection con) {
        connection = con;
        proxy = createSimpleProxy(ResourceManagedConnection.class, new ConnectionHandler());
    }

    /**
     * DBと接続し、リソース管理機能を持つコネクションを返す
     * 
     * @return
     * @throws java.sql.SQLException
     */
    public static ResourceManagedConnection connect() throws SQLException {
        File db = new File(dbPath);
        if (!db.exists()) {
            throw new RuntimeException(new FileNotFoundException("データベースが存在しません: "
                    + db.getAbsolutePath()));
        }
        Connection con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        DBManager manager = new DBManager(con);
        return (ResourceManagedConnection) manager.getProxy();
    }

    /**
     * cのProxyを作成
     * 
     * @param c
     * @param handler
     * @return
     */
    private static Object createSimpleProxy(Class<?> c, InvocationHandler handler) {
        return Proxy.newProxyInstance(c.getClassLoader(), new Class[] { c }, handler);
    }

    /**
     * 使用したリソースを閉じる
     * 
     * @throws java.sql.SQLException
     */
    private void closeResources() throws SQLException {
        for (Statement s : statements) {
            s.close();
        }
        for (ResultSet r : resultSets) {
            r.close();
        }
    }

    public Object getProxy() {
        return proxy;
    }

    /**
     * o.method(args)を実行
     * 
     * @param o
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    private Object doMethod(Object o, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(o, args);
        } catch (InvocationTargetException e) {
            throw (e.getCause() == null) ? e : e.getCause();
        }
    }

    /**
     * コネクションのハンドラ
     * @author rtakizawa
     */
    private class ConnectionHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (isCloseMethod(method) || isFinalizeMethod(method)) {
                closeResources();
            }
            Object o = doMethod(connection, method, args);
            if (o instanceof Statement) {
                statements.add((Statement) o);
                o = createProxyStatement((Statement) o);
            }
            return o;
        }

        private boolean isCloseMethod(Method m) {
            return m.getName().equals("close") && (m.getParameterTypes().length == 0);
        }

        private boolean isFinalizeMethod(Method m) {
            return m.getName().equals("finalize") && (m.getParameterTypes().length == 0);
        }

        private Object createProxyStatement(Statement o) {
            Object proxy;
            if (o instanceof PreparedStatement) {
                proxy = new StatementHandler(o, PreparedStatement.class).getProxy();
            } else if (o instanceof CallableStatement) {
                proxy = new StatementHandler(o, CallableStatement.class).getProxy();
            } else {
                proxy = new StatementHandler(o, Statement.class).getProxy();
            }
            return proxy;
        }
    }

    /**
     * Statementのハンドラ
     * @author rtakizawa
     */
    private class StatementHandler implements InvocationHandler {

        private Object original;
        private Object proxy;

        public StatementHandler(Object o, Class<?> c) {
            original = o;
            proxy = createSimpleProxy(c, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object o = doMethod(original, method, args);
            if (o instanceof ResultSet) {
                resultSets.add((ResultSet) o);
            }
            return o;
        }

        public Object getProxy() {
            return proxy;
        }

    }

}
