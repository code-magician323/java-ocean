FROM mcr.microsoft.com/dotnet/core/sdk:2.2 AS build-env
WORKDIR /app

# Copy csproj and restore as distinct layers
COPY PatronManagement.Core/PatronManagement.Core.* ./
COPY PatronManagement.Core/nlog.config ./
COPY PatronManagement.Core/*.json ./
COPY PatronManagement.Core/patronimages ./patronimages
RUN dotnet restore

# Copy everything else and build
COPY . ./
RUN dotnet publish -c Release -o out
# ARG ASPNETCORE_ENVIRONMENT
# RUN dotnet publish -c Release  /p:EnvironmentName=$ASPNETCORE_ENVIRONMENT  -o out

# Build runtime image
FROM mcr.microsoft.com/dotnet/core/aspnet:2.2
WORKDIR /app

COPY --from=build-env /app/out .
COPY --from=build-env /app/nlog.config .
COPY --from=build-env /app/appsettings.azureqa1.json .
COPY --from=build-env /app/appsettings.azureqa2.json .
COPY --from=build-env /app/appsettings.dev1.json .
COPY --from=build-env /app/appsettings.json .
COPY --from=build-env /app/appsettings.qa1.json .
COPY --from=build-env /app/patronimages/* ./patronimages/

EXPOSE 5000
EXPOSE 80
ENTRYPOINT ["dotnet", "PatronManagement.Core.dll"]