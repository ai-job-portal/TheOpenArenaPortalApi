// src/services/jobSeekerService.js
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api'; // Default

export const registerJobSeeker = async (jobSeekerData) => {
  const response = await fetch(`${API_BASE_URL}/auth/register/jobseeker`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(jobSeekerData),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Registration failed');
  }

  return response.json();
};