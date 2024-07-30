import React, { createContext, useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../utils/axios-config';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [user_email, setUserEmail] = useState(null);
  const navigate = useNavigate(); 

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    const storedUserEmail = localStorage.getItem('user_email');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    if (storedUserEmail) {
      setUserEmail(storedUserEmail);
    }
  }, []);

  const login = async (email, password) => {
    try {
      const response = await axiosInstance.post('/auth/authenticate', { email, password });
      setUser(response.data);
      localStorage.setItem('user', JSON.stringify(response.data));
      localStorage.setItem('user_email', email);
      return response.data;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  const register = async (userData) => {
    try {
      const response = await axiosInstance.post('/auth/register', userData);
      //console.log(response.data);
      return response.data;
    } catch (error) {
      console.error('Registration failed:', error);
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    setUserEmail(null);
    localStorage.removeItem('user');
    localStorage.removeItem('user_email');
    delete axiosInstance.defaults.headers['Authorization'];
    navigate('/login');
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout,user_email }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);