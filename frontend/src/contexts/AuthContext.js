import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        const storedToken = localStorage.getItem('token');

        if (storedUser && storedToken) {
            try {
                const parsedUser = JSON.parse(storedUser);
                setUser(parsedUser);
                setToken(storedToken);
                axios.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
                console.log("AuthContext (useEffect): Token postavljen u Axios default headers.");
            } catch (e) {
                console.error("AuthContext (useEffect): Greška pri parsiranju korisničkih podataka iz localStorage-a:", e);
                localStorage.removeItem('user');
                localStorage.removeItem('token');
                setUser(null);
                setToken(null);
                delete axios.defaults.headers.common['Authorization'];
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            setLoading(true);
            const response = await axios.post('http://localhost:8081/auth/login', { email, password });

            console.log("AuthContext (Login Response): Full response data FROM BACKEND:", response.data);

            const { token: receivedToken, email: userEmail, roles, fullName, pacijentID, doktorID, grad, specijalizacije, ocjena, iskustvo, radnoVrijeme, userId } = response.data;

            const userData = {
                email: userEmail,
                fullName: fullName,
                roles: roles || [],
                pacijentId: pacijentID || null, // Koristi pacijentID (iz backend response)
                doktorId: doktorID || null,
                grad: grad || null,
                specijalizacije: specijalizacije || [],
                ocjena: ocjena || null,
                iskustvo: iskustvo || null,
                radnoVrijeme: radnoVrijeme || null,
                userId: userId || null, // Ovo je Auth User ID (Long)
            };

            console.log("AuthContext (Login Response): Constructed userData object (before setUser):", userData);


            setUser(userData);
            setToken(receivedToken);
            localStorage.setItem('user', JSON.stringify(userData));
            localStorage.setItem('token', receivedToken);
            axios.defaults.headers.common['Authorization'] = `Bearer ${receivedToken}`;
            console.log("AuthContext: Token postavljen u Axios default headers nakon logina.");
            setLoading(false);
            return response.data;
        } catch (error) {
            setLoading(false);
            console.error('Login error in AuthContext:', error.response?.data || error.message);
            throw error;
        }
    };

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        delete axios.defaults.headers.common['Authorization'];
    };

    return (
        <AuthContext.Provider value={{ user, token, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};
