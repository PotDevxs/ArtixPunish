# ArtixPunish

Plugin de **punições** para servidores **Spigot** (testado em **1.8.8**). Inclui ban (permanente e temporário), mute, avisos, kick, ban por IP, histórico, **menus em inventário** para staff e mensagem de tela de ban configurável (estilo redes grandes).

Repositório: [github.com/PotDevxs/ArtixPunish](https://github.com/PotDevxs/ArtixPunish)

---

## Requisitos

- **Java 8** (compatível com `source/target 1.8` do projeto)
- Servidor **Spigot** ou fork compatível com API **1.8.8**

---

## Compilar

```bash
mvn clean package
```

O JAR gerado fica em `target/ArtixPunish-1.0.jar` (versão conforme `pom.xml`).

---

## Instalação

1. Copie o JAR para a pasta `plugins/` do servidor.
2. Inicie o servidor (ou use `/reload` apenas em ambiente de teste).
3. Edite `plugins/ArtixPunish/config.yml` conforme necessário.
4. Ajuste permissões no teu plugin de permissões (LuckPerms, etc.).

---

## Funcionalidades

| Área | Descrição |
|------|-----------|
| **Ban** | Ban permanente e temporário por UUID; expiração automática de bans temporários ao tentar entrar. |
| **Mute** | Mute permanente e temporário; bloqueio de chat com mensagens configuráveis. |
| **Kick** | Expulsar jogador **online**. |
| **Warn** | Sistema de avisos com contagem por jogador. |
| **IP ban** | Ban por endereço IP (expulsa jogadores com esse IP). |
| **Histórico** | Log das punições em ficheiro + comando para ver histórico por jogador. |
| **Menus (GUI)** | Inventários para punir jogadores online e para **limpar todos os bans** (UUID e/ou IP), com confirmação. |
| **Comandos dinâmicos** | Comandos registados em runtime (`RegisterManager`), sem lista enorme no `plugin.yml`. |
| **Tela de ban** | Mensagem ao ser kickado por ban configurável em várias linhas (`ban-screen-lines`), com Discord, separadores e placeholders. |

### Armazenamento (YAML)

Dados em `plugins/ArtixPunish/data/`:

| Ficheiro | Conteúdo |
|----------|----------|
| `bans.yml` | Bans por jogador e por IP |
| `mutes.yml` | Mutes |
| `warns.yml` | Avisos |
| `history.yml` | Últimas entradas de histórico (limite interno) |

---

## Comandos

Os comandos são registados pelo código; **aliases** entre parênteses.

| Comando | Alias | Uso resumido |
|---------|-------|----------------|
| `/ban` | `/b` | `/ban <jogador> [motivo]` |
| `/tempban` | `/tb` | `/tempban <jogador> <duração> [motivo]` — duração: `7d`, `12h`, `30m`, `2w`, `60s` |
| `/unban` | — | `/unban <jogador>` |
| `/kick` | `/k` | `/kick <jogador> [motivo]` |
| `/mute` | — | `/mute <jogador> [motivo]` |
| `/tempmute` | `/tm` | `/tempmute <jogador> <duração> [motivo]` |
| `/unmute` | — | `/unmute <jogador>` |
| `/warn` | — | `/warn <jogador> [motivo]` |
| `/clearwarns` | — | `/clearwarns <jogador>` |
| `/historico` | `/phist`, `/punishhist` | `/historico <jogador>` |
| `/ipban` | — | `/ipban <ip> [motivo]` |
| `/unbanip` | — | `/unbanip <ip>` |
| `/artixpunish reload` | `/apunish`, `/artixp` | Recarrega `config.yml` e ficheiros em `data/` |
| `/punishmenu` | `/punicao`, `/pmenu`, `/punishgui` | Abre o menu de punições (só jogador) |

**Motivo por defeito** (comandos): `defaults.reason` no `config.yml`.  
**Motivo no menu**: `menu.default-reason`.

---

## Permissões

| Permissão | Função |
|-----------|--------|
| `artixpunish.*` | Todas as permissões abaixo |
| `artixpunish.ban` | Ban permanente |
| `artixpunish.tempban` | Ban temporário |
| `artixpunish.unban` | Remover ban |
| `artixpunish.kick` | Kick |
| `artixpunish.mute` | Mute permanente |
| `artixpunish.tempmute` | Mute temporário |
| `artixpunish.unmute` | Remover mute |
| `artixpunish.warn` | Avisar |
| `artixpunish.clearwarns` | Limpar avisos |
| `artixpunish.history` | Ver histórico |
| `artixpunish.ipban` | Ban por IP |
| `artixpunish.unbanip` | Remover ban de IP |
| `artixpunish.reload` | Recarregar plugin |
| `artixpunish.menu` | Abrir `/punishmenu` |
| `artixpunish.menu.reset.players` | No menu: limpar **todos** os bans por jogador (UUID) |
| `artixpunish.menu.reset.ip` | No menu: limpar **todos** os bans por IP |

Por defeito no `plugin.yml`, estas permissões estão com `default: op`.

---

## Configuração (`config.yml`)

- **Mensagens** com cores `&` e placeholders (`%player%`, `%reason%`, `%staff%`, etc.).
- **`ban-screen-lines`**: lista de linhas mostradas ao jogador banido ao tentar entrar (multi-linha). Placeholders: `%player%`, `%reason%`, `%staff%`, `%discord%`, `%banTypeLine%`, `%expiresLine%`.
- **`ban-screen-discord`**: texto do Discord na tela de ban.
- Tipos de ban (`ban-screen-kind-*`) e linhas de expiração (`ban-screen-line-expires-*`).
- Se `ban-screen-lines` estiver vazio, usa-se o formato antigo com `ban-screen` + `ban-screen-permanent` / `ban-screen-expires`.

---

## Arquitetura (resumo)

- **`CommandArgs`** — contexto e utilitários dos comandos (argumentos, `join`, `defaultReason`, jogadores).
- **`CommandHandler`** — interface `handle(CommandArgs)`.
- **`RegisterManager`** — registo de comandos no `CommandMap` (reflexão), sem poluir `plugin.yml` com dezenas de comandos.
- **`PunishmentService`** — lógica partilhada entre comandos e menus.
- **GUI** — `MenuFactory`, `MenuListener`, `ArtixMenuHolder` para inventários de punição e reset global.

---

## Licença

Define a licença no repositório se quiseres uso por terceiros (por exemplo MIT, GPL).

---

## Autor

Plugin **ArtixPunish** — ver `plugin.yml` para `author` atual.
