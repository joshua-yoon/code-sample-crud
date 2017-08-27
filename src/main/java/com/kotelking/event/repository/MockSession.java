package com.kotelking.event.repository;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by joshua on 2017. 7. 29..
 */
public class MockSession implements SqlSession {
    @Override
    public <T> T selectOne(String s) {
        return null;
    }

    @Override
    public <T> T selectOne(String s, Object o) {
        return null;
    }

    @Override
    public <E> List<E> selectList(String s) {
        return null;
    }

    @Override
    public <E> List<E> selectList(String s, Object o) {
        return null;
    }

    @Override
    public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
        return null;
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, String s1) {
        return null;
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
        return null;
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
        return null;
    }

    @Override
    public <T> Cursor<T> selectCursor(String s) {
        return null;
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o) {
        return null;
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o, RowBounds rowBounds) {
        return null;
    }

    @Override
    public void select(String s, Object o, ResultHandler resultHandler) {

    }

    @Override
    public void select(String s, ResultHandler resultHandler) {

    }

    @Override
    public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {

    }

    @Override
    public int insert(String s) {
        return 0;
    }

    @Override
    public int insert(String s, Object o) {
        return 0;
    }

    @Override
    public int update(String s) {
        return 0;
    }

    @Override
    public int update(String s, Object o) {
        return 0;
    }

    @Override
    public int delete(String s) {
        return 0;
    }

    @Override
    public int delete(String s, Object o) {
        return 0;
    }

    @Override
    public void commit() {

    }

    @Override
    public void commit(boolean b) {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void rollback(boolean b) {

    }

    @Override
    public List<BatchResult> flushStatements() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void clearCache() {

    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public <T> T getMapper(Class<T> aClass) {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
