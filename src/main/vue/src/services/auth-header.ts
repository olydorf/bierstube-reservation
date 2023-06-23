export default function authHeader(): Record<string, string> {
  const user = JSON.parse(<string>localStorage.getItem("user"));

  if (user && user.accessToken) {
    return { Authorization: `Bearer ${user.accessToken}` }; // for Spring Boot back-end
  } else {
    return {};
  }
}
