import axios, { AxiosResponse } from "axios";

const API_URL = "http://localhost:8080/start/";

class AuthService {
  async login(username: string, password: string): Promise<any> {
    return axios
        // todo I had to change this to fix the Bad Request responses
      .post(API_URL + "login?password=" + password + "&username=" + username )
      .then((response: AxiosResponse) => {
        if (response.data.accessToken) {
          localStorage.setItem("user", JSON.stringify(response.data));
        }

        return response.data;
      });
  }

  logout(): void {
    localStorage.removeItem("user");
  }
}

const authService = new AuthService();
export default authService;
