import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import axiosInstance from '../utils/axios-config';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const { user, logout ,user_email } = useAuth();
  const [demoContent, setDemoContent] = useState('');
  const navigate = useNavigate();
  useEffect(() => {
    if (!user || !user_email) {
      navigate('/login');
      return;
    }

    fetchDemoContent();
  }, [user, navigate,user_email]);

  const fetchDemoContent = async () => {
    try {
      const response = await axiosInstance.get('/demo-controller');
      setDemoContent(response.data);
    } catch (error) {
      console.error('Error fetching demo content:', error);
      if (error.response && error.response.status === 401) {
        logout();
      }
    }
  };

  const handleLogout = () => {
    logout();  // เรียกใช้ logout จาก AuthContext ซึ่งจะจัดการการ navigate ให้
  };

  if (!user || !user_email) {
    return null;  // หรือแสดง loading indicator
  }



  return (
    <div>
      <h1>Welcome to Dashboard</h1>
      <p>Email: {user_email}</p>
      <p>Demo Content: {demoContent}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
}

export default Dashboard;