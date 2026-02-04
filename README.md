# TodoList App 📝

Aplicativo Android de lista de tarefas desenvolvido para o trabalho da disciplina, utilizando **Jetpack Compose** e **Firebase**.

## Funcionalidades

- ✅ Login e cadastro de usuários (Firebase Authentication)
- ✅ Criar, editar e excluir tarefas
- ✅ Marcar tarefas como concluídas
- ✅ Cada usuário tem sua própria lista de tarefas (Firestore)
- ✅ Interface moderna com Jetpack Compose

## Tecnologias Utilizadas

- **Jetpack Compose** - UI declarativa
- **Firebase Authentication** - Login/cadastro de usuários
- **Firebase Firestore** - Armazenamento das tarefas
- **Room** - Banco de dados local (usado inicialmente)
- **MVVM** - Arquitetura com ViewModels
- **Navigation Compose** - Navegação entre telas
- **Kotlin Coroutines** - Programação assíncrona

## Telas

1. **Login** - Autenticação do usuário
2. **Cadastro** - Criação de nova conta
3. **Lista de Tarefas** - Visualização e gerenciamento das tarefas
4. **Adicionar/Editar Tarefa** - Formulário para criar ou editar

## Organização de Pastas

```text
com.example.todolist/
├── data/           # Entidades e repositórios
├── domain/         # Modelos de dados
├── navigation/     # Configuração de navegação
└── ui/             # Camada de apresentação
    ├── feature/    # Telas e ViewModels
    │   ├── addEdit/
    │   ├── list/
    │   └── pages/  # Login e Signup
    ├── components/ # Componentes reutilizáveis
    └── theme/      # Cores e estilos
