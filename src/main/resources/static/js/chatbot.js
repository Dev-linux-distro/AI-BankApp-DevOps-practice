document.addEventListener("DOMContentLoaded", () => {

    const chatToggle = document.getElementById("chat-toggle");
    const chatWindow = document.getElementById("chat-window");
    const closeChat = document.getElementById("close-chat");

    const chatInput = document.getElementById("chat-input");
    const sendChat = document.getElementById("send-chat");

    const chatMessages = document.getElementById("chat-messages");


    // Open chatbot
    chatToggle.addEventListener("click", () => {

        chatWindow.style.display = "flex";

        chatInput.focus();
    });


    // Close chatbot
    closeChat.addEventListener("click", () => {

        chatWindow.style.display = "none";
    });


    // Send message
    async function sendMessage() {

        const message = chatInput.value.trim();

        if (!message) {
            return;
        }


        // Add user message
        const userMessage = document.createElement("div");

        userMessage.className = "user-message";

        userMessage.textContent = message;

        chatMessages.appendChild(userMessage);


        // Clear input
        chatInput.value = "";


        // Scroll
        chatMessages.scrollTop = chatMessages.scrollHeight;


        // Show thinking message
        const botMessage = document.createElement("div");

        botMessage.className = "bot-message";

        botMessage.textContent = "Thinking...";

        chatMessages.appendChild(botMessage);


        try {

            // Get CSRF token from the page
            const csrfToken =
                document.querySelector('meta[name="_csrf"]').getAttribute("content");

            const csrfHeader =
                document.querySelector('meta[name="_csrf_header"]').getAttribute("content");


            // Send message to Spring Boot
            const response = await fetch("/ai/chat", {

                method: "POST",

                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },

                body: JSON.stringify({
                    message: message
                })

            });


            if (!response.ok) {

                throw new Error(
                    `HTTP error: ${response.status}`
                );

            }


            const data = await response.json();


            botMessage.textContent =
                data.response || "No response received.";


        } catch (error) {

            console.error("AI Chat Error:", error);

            botMessage.textContent =
                "Sorry, I couldn't connect to the AI assistant.";

        }


        chatMessages.scrollTop =
            chatMessages.scrollHeight;
    }


    // Send button
    sendChat.addEventListener("click", sendMessage);


    // Enter key
    chatInput.addEventListener("keydown", (event) => {

        if (event.key === "Enter") {

            sendMessage();

        }

    });

});