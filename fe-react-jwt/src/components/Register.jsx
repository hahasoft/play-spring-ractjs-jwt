import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

function Register() {
  const [userData, setUserData] = useState({
    firstname: '',
    lastname: '',
    email: '',
    password: '',
    role: 'USER'
  });
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setUserData({ ...userData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await register(userData);
      navigate('/login');
    } catch (error) {
      console.error('Registration error:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="text" name="firstname" value={userData.firstname} onChange={handleChange} placeholder="First Name" required />
      <input type="text" name="lastname" value={userData.lastname} onChange={handleChange} placeholder="Last Name" required />
      <input type="email" name="email" value={userData.email} onChange={handleChange} placeholder="Email" required />
      <input type="password" name="password" value={userData.password} onChange={handleChange} placeholder="Password" required />
      <select name="role" value={userData.role} onChange={handleChange}>
        <option value="USER">User</option>
        <option value="MANAGER">Manager</option>
        <option value="ADMIN">Admin</option>
      </select>
      <button type="submit">Register</button>
    </form>
  );
}

export default Register;